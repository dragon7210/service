package com.example.springboot.service;


import com.example.springboot.exception.FileStorageException;
import com.example.springboot.models.VegModel;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    public String storeFile(MultipartFile file, String event_type) {
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
            Document document = documentBuilder.parse(xmlfile);
            String firstname = null, lastname = null, address1 = null, country = null, city = null, state = null, zip_code = null;
            NodeList list = document.getElementsByTagName("ExamSchedulingRequestCreatedEvent");
            if(list.getLength()>0){
                if (list.item(0).getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) list.item(0);
                    firstname = element.getAttribute("examSchedulingRequestUuid");
                    ESR_inbound_filter_model esr_inbound_filter_model = new ESR_inbound_filter_model();
                    esr_inbound_filter_model_repository.save(esr_inbound_filter_model);
                }
            }
            return fileName;
        } catch (IOException | SAXException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
