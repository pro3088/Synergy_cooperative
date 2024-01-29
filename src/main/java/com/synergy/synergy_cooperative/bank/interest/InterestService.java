package com.synergy.synergy_cooperative.bank.interest;

import groovy.beans.PropertyReader;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Service
public class InterestService {

    ModelMapper mapper = new ModelMapper();

    Properties properties = new Properties();
    protected static Logger log = LoggerFactory.getLogger(InterestService.class);

    public InterestDTO getInterest(){
        int interest = 0;
        try (InputStream inputStream = PropertyReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                interest = Integer.parseInt(properties.getProperty("transaction.interest"));
            }
        } catch ( IOException e) {
            log.error("Exception getting file {}", e.getMessage());
        }
        return new InterestDTO(interest);
    }

    public InterestDTO setInterest(int interest){
        try (InputStream inputStream = PropertyReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                properties.setProperty("transaction.interest", String.valueOf(interest));
                try (FileOutputStream fileOutputStream = new FileOutputStream("config.properties")) {
                    properties.store(fileOutputStream, null);
                } catch (IOException e) {
                    log.error("Exception setting file {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Exception getting file {}", e.getMessage());
        }
        return new InterestDTO(interest);
    }
}
