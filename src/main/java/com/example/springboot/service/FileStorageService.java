package com.example.springboot.service;


import com.example.springboot.constant.Constant;
import com.example.springboot.exception.FileStorageException;
import com.example.springboot.models.ESR_inbound_filter_model;
import com.example.springboot.property.FileStorageProperties;
import com.example.springboot.repositories.ESR_inbound_filter_model_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    @Autowired
    private final ESR_inbound_filter_model_repository esr_inbound_filter_model_repository;

    public FileStorageService(ESR_inbound_filter_model_repository esr_inbound_filter_model_repository) {
        this.esr_inbound_filter_model_repository = esr_inbound_filter_model_repository;
        fileStorageLocation = null;
    }
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, ESR_inbound_filter_model_repository esr_inbound_filter_model_repository) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        esr_inbound_filter_model_repository = null;
        this.esr_inbound_filter_model_repository = esr_inbound_filter_model_repository;
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
                    ESR_inbound_filter_model esr_inbound_filter_model = new ESR_inbound_filter_model(eventType,uuid,"a","NO","b",new Date(),new Date());
                    esr_inbound_filter_model_repository.save(esr_inbound_filter_model);
                    Files.move(xmlFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File move: "+xmlFile.getFileName()+"   "+destinationPath.toString());
                }
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
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
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String filepath = targetLocation.toString();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            File xmlfile = new File(filepath);
            System.out.println("File Path    :"  +filepath);
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
                ESR_inbound_filter_model esr_inbound_filter_model = new ESR_inbound_filter_model(eventType,uuid,"a","b","c",new Date(),new Date());
                esr_inbound_filter_model_repository.save(esr_inbound_filter_model);
            }
            return fileName;
        } catch (IOException | SAXException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
