package com.example.springboot.service;


import com.example.springboot.exception.FileStorageException;
import com.example.springboot.models.VegModel;
import com.example.springboot.property.FileStorageProperties;
import com.example.springboot.repositories.VegRepository;
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
    private final VegRepository vegRepository;

    public FileStorageService(VegRepository vegRepository) {
        this.vegRepository = vegRepository;
        fileStorageLocation = null;
    }
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, VegRepository vegRepository) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        vegRepository = null;
        this.vegRepository = vegRepository;
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
            Document document = documentBuilder.parse(xmlfile);
            String firstname = null, lastname = null, address1 = null, country = null, city = null, state = null, zip_code = null;
            NodeList list = document.getElementsByTagName("VeteranServiceMemberInfo");
            if(list.getLength()>0){
                if (list.item(0).getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) list.item(0);
                    firstname = element.getAttribute("firstName");
                    lastname = element.getAttribute("lastName");
                    address1 = element.getElementsByTagName("Address1").item(0).getTextContent();
                    country = element.getElementsByTagName("Country").item(0).getTextContent();
                    city = element.getElementsByTagName("City").item(0).getTextContent();
                    zip_code = element.getElementsByTagName("ZipOrPostalCode").item(0).getTextContent();
                    state = element.getElementsByTagName("State").item(0).getTextContent();
                    VegModel vegModel = new VegModel(firstname, lastname, address1, city, state, zip_code, country);
                    vegRepository.save(vegModel);
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
