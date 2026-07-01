package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.*;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        Set<String> uniqueSpecies = new TreeSet<>();
        Map<String, List<Animal>> animalsBySpecies = new HashMap<>();
        List<String> animalsNeedingVetInput = new ArrayList<>();

        // - uniqueSpecies
        for(Animal animal: allAnimals){
            uniqueSpecies.add(animal.getSpecies());
        }

        // - animalsBySpecies

        for(Animal animal: allAnimals){
            String species = animal.getSpecies();
            if(!animalsBySpecies.containsKey(species)){
                animalsBySpecies.put(species, new ArrayList<>());
            }
            animalsBySpecies.get(species).add(animal);
        }
        // - animalsNeedingVetInput with format name(species)

        for(Animal animal: allAnimals){
            if(animal.getAge() == null){
                animalsNeedingVetInput.add(animal.getName() + "(" + animal.getSpecies() + ")");
            }
        }


        // TODO Step 3:
        // Add necessary fields to ShelterReportData
        // Use stream pipelines for:
        // - vaccinated vs unvaccinated counts per species
        // - oldest animal per species (excluding unknown ages)

        return new ShelterReportData(importResult);
    }
}
