package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
public class CsvImportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();

        // 1) Read intake.csv with UTF-8.
        int skippedRows = 0;

        try(BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)){

            // 2) Skip header row.
            br.readLine();

            String line;
            int count = 0;

            while((line = br.readLine()) != null){
                count++;
                // 3) Skip malformed rows and log warnings.
                String[] rows = line.split(",");

                if(rows.length != 5){
                    log.warn("Required rows missing, skipping in line number '{}'", count);
                    skippedRows++;
                    continue;
                }

                String name = rows[0];
                String species = rows[1];
                String age = rows[2];
                String vaccinated = rows[3];
                String intakeDate = rows[4];

                Integer ageAsInt = null;

                if(name == null || name.isBlank() || name.matches(".*\\d.*")){
                    log.warn("Incorrect name in line '{}'", count);
                    skippedRows++;
                    continue;
                }

                if(species == null || species.isBlank() || species.matches(".*\\d.*")){
                    log.warn("Incorrect specie name in line '{}'", count);
                    skippedRows++;
                    continue;
                }
                // 4) Allow blank age as unknown (null), but reject non-numeric age values.
                if(age.isBlank()){
                    age = null;
                } else if(!isNumeric(age)){
                    log.warn("Age is not a numeric value '{}'",age);
                    skippedRows++;
                    continue;
                }else{
                    ageAsInt = Integer.parseInt(age);
                }

                if(vaccinated == null || vaccinated.isBlank() || !vaccinated.toLowerCase().matches("^(true|false)$")){
                    log.warn("Incorrected vaccinated field value in line '{}'", count);
                    skippedRows++;
                    continue;
                }

                // 5) Parse intakeDate using DateTimeFormatter.

                if(!isRightFormat(intakeDate)){
                    log.warn("Incorrect date format in line '{}'", count);
                    skippedRows++;
                    continue;
                }
                // 6) Map each row to Animal object.
                Animal animal = new Animal(name, species, ageAsInt, Boolean.parseBoolean(vaccinated), LocalDate.parse(intakeDate, DATE_FORMAT));
                allAnimals.add(animal);
            }

        }catch (Exception e){
            log.error("Reading failed", e);
        }

        int importedRows = allAnimals.size();
        return new ImportResult(allAnimals, skippedRows, importedRows);

    }
    private boolean isNumeric(String str){
        if(str == null){
            return false;
        }

        try{
            Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private boolean isRightFormat(String inputDate){
        try{
            LocalDate.parse(inputDate, DATE_FORMAT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
