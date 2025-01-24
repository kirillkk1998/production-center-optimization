import model.ProductionLine;
import util.ExcelReader;
import util.CsvWriter;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar program.jar <input.xlsx> <output.csv>");
            return;
        }
        
        try {
            String inputFile = args[0];
            String outputFile = args[1];
            
            // Читаем данные из Excel
            ProductionLine productionLine = ExcelReader.readFromExcel(inputFile);
            
            // Запускаем симуляцию
            productionLine.simulate();
            
            // Записываем результаты
            CsvWriter.writeResults(outputFile, productionLine.getEvents());
            
            System.out.println("Simulation completed successfully!");
            
            System.out.println("\nSimulation Statistics:");
            System.out.println("Total simulation time: " + productionLine.getTotalTime() + " minutes");
            for (ProductionCenter center : productionLine.getCenters()) {
                System.out.println("\n" + center.getStatistics());
            }
            
        } catch (Exception e) {
            System.err.println("Error during simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 