concrete InstrucsSolr of Instrucs = {

  lincat
    Instruction = Str ;
    Subject = Str ;
    Relation = Str ;
    Object = Str ;

  lin
    MkInstruction subject relation = "q=" ++ subject ++ "AND" ++ relation ;
    People = "object_type : Person" ;
    Know object = "expertise : " ++ object ;
    Java = "Java" ;
}
