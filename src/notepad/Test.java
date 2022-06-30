package notepad;

import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;

public class Test {
    
   
    public static void main(String[] args) {
        
        
        try{
                Path path=Path.of(ClassLoader.getSystemResource("notepad/settings.json").toURI());
                String text=Files.readString(path);
                   JSONObject json=new JSONObject(text);
                
               json.put("FontName", "Fira Code");
                
                Files.writeString(path, json.toString());
                
//                String name=json.get("FontName");
                
                System.out.println();

            }catch(Exception ex){
                ex.printStackTrace();
            }
           
       
           
      
    }
}
