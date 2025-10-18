package com.analytics.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.analytics.entity.Passenger;
import com.analytics.repository.PassengerRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

/**
 * Loads the Titanic dataset from CSV into the database on application startup.
 * Runs once each time the application starts to populate the in-memory database.
 */
@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Override
    public void run(String... args) throws Exception {
        loadTitanicData();
    }
    
    /**
     * Reads the Titanic CSV file and inserts all passenger records into the database.
     */
    private void loadTitanicData() {
        try {
            ClassPathResource resource = new ClassPathResource("titanic.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()));
            
            List<String[]> records = reader.readAll();
            reader.close();
            
            List<Passenger> passengers = new ArrayList<>();
            
            // Skip header row
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                
                if (record.length >= 12) {
                    Passenger passenger = new Passenger();
                    
                    passenger.setPassengerId(parseInteger(record[0]));
                    passenger.setSurvived(parseInteger(record[1]));
                    passenger.setPclass(parseInteger(record[2]));
                    passenger.setName(record[3]);
                    passenger.setSex(record[4]);
                    passenger.setAge(parseDouble(record[5]));
                    passenger.setSibsp(parseInteger(record[6]));
                    passenger.setParch(parseInteger(record[7]));
                    passenger.setTicket(record[8]);
                    passenger.setFare(parseDouble(record[9]));
                    passenger.setCabin(record[10].isEmpty() ? null : record[10]);
                    passenger.setEmbarked(record[11].isEmpty() ? null : record[11]);
                    
                    passengers.add(passenger);
                }
            }
            
            passengerRepository.saveAll(passengers);
            System.out.println("Loaded " + passengers.size() + " passenger records");
            
        } catch (IOException | CsvException e) {
            System.err.println("Error loading Titanic data: " + e.getMessage());
        }
    }
    
    private Integer parseInteger(String value) {
        try {
            return value.isEmpty() ? null : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Double parseDouble(String value) {
        try {
            return value.isEmpty() ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}