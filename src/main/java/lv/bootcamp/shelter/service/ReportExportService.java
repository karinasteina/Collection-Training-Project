package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) {
        // 1) Write upload-report.txt in required format.
        try (BufferedWriter bw = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)){
            bw.write("Shelter Report Data");
            bw.newLine();

            // 2) Include generated date, imported/skipped totals.
            bw.write("Date:" + LocalDate.now());
            bw.newLine();

            bw.write("Imported lines: " + reportData.totalImported());
            bw.newLine();

            bw.write("Skipped lines: " + reportData.totalSkipped());
            bw.newLine();


            bw.write("Unique species: " );
            bw.newLine();

            for(String species : reportData.uniqueSpecies()){
                int count = reportData.animalsBySpecies().get(species).size();
                bw.write(" " + species + ": " + count);
                bw.newLine();
            }

            // 4) Include oldest animal per species.

            bw.write("Oldest animal per species: ");
            bw.newLine();

            for(Map.Entry<String, Optional<Animal>> data : reportData.oldestAnimalBySpecies().entrySet()){
                bw.write(" " + data.getKey() + ": " + data.getValue().get().getName());
                bw.newLine();
            }

            // 5) Include animalsNeedingVetInput as name(species), name2(species2).

            bw.write("Animals needing vet input: ");
            bw.write(String.join(", ", reportData.animalsNeedingVetInput()));
            bw.newLine();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
