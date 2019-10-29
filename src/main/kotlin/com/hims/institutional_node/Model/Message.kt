package com.hims.institutional_node.Model

data class Message(var receiver:String?, var sender:String?, var value:String?, var key_no:Long?, var aes_key:String?, var sha_key:String?)