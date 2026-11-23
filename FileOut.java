import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io. FileWriter;



public class FileOut {
    public String getFIleSource(String assemblyCodeGenerated){

        try {
            Path fileOutput = Path.of("example-repo/assembly_code.asm");
            Files.writeString(fileOutput, assemblyCodeGenerated);
            System.out.println("System successfully generated to: " + fileOutput);
            return assemblyCodeGenerated;

        } catch(Exception e){
            System.out.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
        }

        return "false";

        
    }
}
