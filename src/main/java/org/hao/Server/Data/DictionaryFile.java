package org.hao.Server.Data;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class DictionaryFile {
    public static void SaveFile(Dictionary dictionary){
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("dictionary.json"), dictionary);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Dictionary ReadFile(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File("dictionary.json"), org.hao.Server.Data.Dictionary.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
