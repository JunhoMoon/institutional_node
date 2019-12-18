package com.hims.institutional_node

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Component
internal class FileManager{
     var root_path = "C:/institution_uploads"
    var attach_path = "/"

    fun saveFile(file: MultipartFile):String{
        var fileName = System.currentTimeMillis().toString() + file.originalFilename
        file.transferTo(Paths.get(root_path+attach_path+fileName))

        return fileName
    }

    fun saveFileBybyte(fileName:String, fileString:String){
        var fileByte = Base64.getDecoder().decode(fileString)
        try {
            Files.write(Paths.get(root_path+attach_path+fileName), fileByte)
        } catch (e: IOException){
            println("saveFileBybyte error : $e")
        }
    }

    fun fileToByte(fileName: String):String{
        var fileByte = Files.readAllBytes(Paths.get(root_path+attach_path+fileName))
        var fileString = Base64.getEncoder().encodeToString(fileByte)
        return fileString
    }
}