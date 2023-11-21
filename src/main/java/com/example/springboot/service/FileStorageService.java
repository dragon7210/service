package com.example.springboot.service;

import com.example.springboot.constant.Constant;
import com.example.springboot.models.ESR_inbound_filter_model;
import com.example.springboot.models.esr_inbound_filter_config_model;
import com.example.springboot.repositories.ESR_inbound_filter_model_repository;
import com.example.springboot.repositories.esr_inbound_filter_config_model_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    @Autowired
    private final ESR_inbound_filter_model_repository esr_inbound_filter_model_repository;
    @Autowired
    private final esr_inbound_filter_config_model_repository esr_inbound_filter_config_model_repository;
    public FileStorageService(ESR_inbound_filter_model_repository esr_inbound_filter_model_repository) {
        this.esr_inbound_filter_model_repository = esr_inbound_filter_model_repository;
        fileStorageLocation = null;
        esr_inbound_filter_config_model_repository = null;
    }

    private void routeToVEMS(String uuid) {
        // Implement routing logic to VEMS
        ESR_inbound_filter_model existingEntity = esr_inbound_filter_model_repository.findByUuid(uuid);
        existingEntity.sent = "VEMS";
        esr_inbound_filter_model_repository.save(existingEntity);
        System.out.println("Routing XML to VEMS");
    }

    private void routeToOMS(String uuid) {
        // Implement routing logic to OMS
        ESR_inbound_filter_model existingEntity = esr_inbound_filter_model_repository.findByUuid(uuid);
        existingEntity.sent = "OMS";
        esr_inbound_filter_model_repository.save(existingEntity);
        System.out.println("Routing XML to OMS");
    }
    public void schdule5mins(String folderPath, String specificPath){

    }
    public void checkXmlfile5min(String folderPath, String specificPath){
        Path path = Paths.get(folderPath);
        System.out.println("File read and write every 5 mins");
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("The provided path is not a directory");
        }

        List<Path> result;
        try (Stream<Path> pathStream = Files.list(path)) {
            result = pathStream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".xml"))
                    .collect(Collectors.toList());

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        result.forEach(xmlFile ->
        {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = null;
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
                File xmlfile = new File(xmlFile.toString());
                Path destinationPath = Paths.get(specificPath).resolve(xmlFile.getFileName());
                Document document = documentBuilder.parse(xmlfile);
                String eventType = null, uuid=null;
                for (String item:Constant.eventType) {
                    NodeList list = document.getElementsByTagName(item);
                    if(list.getLength()>0){
                        if( list.item(0).getNodeType()==Node.ELEMENT_NODE){
                            Element element = (Element) list.item(0);
                            uuid =  element.getAttribute("examSchedulingRequestUuid");
                            eventType = item;
                            break;
                        }
                    }
                }
                if(!eventType.equals("")&&!uuid.equals("")){
                    String sent_to_system = "";
                    ESR_inbound_filter_model existingEntity = esr_inbound_filter_model_repository.findByUuid(uuid);
                    if (existingEntity==null) {
                        List<esr_inbound_filter_config_model> stateInDB;
                        stateInDB = esr_inbound_filter_config_model_repository.findAll();
                        String state = "";
                        String zipcode = "0";
                        String contractId = "";
                        NodeList list = document.getElementsByTagName(eventType);
                        for(int i=0;i<list.getLength();i++) {
                            Node node = list.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) list.item(0);
                                contractId =  element.getAttribute("contractId");
                            }
                        }
                        System.out.println("ContractID: "+ contractId);
                        NodeList addressList = document.getElementsByTagName("PreferredGeoAddress");
                        for(int i=0;i<addressList.getLength();i++){
                            Node node = addressList.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE){
                                Element element = (Element) node;
                                state = element.getElementsByTagName("State").item(0).getTextContent();
                                zipcode = element.getElementsByTagName("ZipOrPostalCode").item(0).getTextContent();
                            }
                        }
                        if(state.equals("") && zipcode.equals("0")){
                            String fallbackState = null, fallbackZip = null;
                            NodeList AddressList1 = document.getElementsByTagName("Address");
                            for(int i=0;i<AddressList1.getLength();i++){
                                Node node = AddressList1.item(i);
                                if (node.getNodeType() == Node.ELEMENT_NODE){
                                    Element element = (Element) node;
                                    fallbackState = element.getElementsByTagName("State").item(0).getTextContent();
                                    fallbackZip = element.getElementsByTagName("ZipOrPostalCode").item(0).getTextContent();
                                }
                            }
                            boolean fallbackStateIsCA = stateInDB.get(0).config_type.equalsIgnoreCase(fallbackState) || "California".equalsIgnoreCase(fallbackState);
                            boolean fallbackZipIsInCA = (Integer. parseInt(fallbackZip)>90001&&Integer. parseInt(fallbackZip)<96162);
                            if ((fallbackStateIsCA || fallbackZipIsInCA) && contractId.contains("_R4_")) {
                                sent_to_system = "VEMS";
                            } else {
                                sent_to_system = "OMS";
                            }
                        }else {
                            boolean stateIsCA = state.equalsIgnoreCase(stateInDB.get(0).config_type) || state.equalsIgnoreCase("California");
                            boolean zipIsInCA = (Integer. parseInt(zipcode)>90001&&Integer. parseInt(zipcode)<96162);
                            boolean contractIdContainsR4 = contractId.contains("_R4_");
//                            System.out.println(":" +state+"    "+zipcode+"    "+contractId);
                            if((stateIsCA || zipIsInCA) && contractIdContainsR4){
                                sent_to_system = "VEMS";
                            }else{
                                sent_to_system = "OMS";
                            }
                        }
                        ESR_inbound_filter_model esr_inbound_filter_model = new ESR_inbound_filter_model(eventType,uuid,"COMPLETED",sent_to_system,"",new Date(),new Date());
                        esr_inbound_filter_model_repository.save(esr_inbound_filter_model);
                        try {
                            Files.move(xmlFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("File move: "+xmlFile.getFileName()+"   "+destinationPath.toString());
                    }
                }else{
                    ESR_inbound_filter_model esr_inbound_filter_model = new ESR_inbound_filter_model("","","ERROR","","Parsing ERROR",new Date(),new Date());
                    esr_inbound_filter_model_repository.save(esr_inbound_filter_model);
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                ESR_inbound_filter_model esr_inbound_filter_model = new ESR_inbound_filter_model("","","ERROR","",e.getMessage(),new Date(),new Date());
                esr_inbound_filter_model_repository.save(esr_inbound_filter_model);
            }

        });



    }
    public void checkXmlfile10min(){
        System.out.println("File read and write every 10 mins");
        List<ESR_inbound_filter_model> esrInboundFilterModels =  esr_inbound_filter_model_repository.findBySent("YES");
        esrInboundFilterModels.forEach(item->{
            System.out.println(item.esr_inbound_filter_pkey+"  "+item.inbound_event_type+" "+item.esr_status+" "+item.sent+" "+item.message);
        });

    }

}
