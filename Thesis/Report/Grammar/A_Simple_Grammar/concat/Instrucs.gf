abstract Instrucs = {
  flags startcat = Instruction;
  cat
    Instruction ; -- An Instruction
    Subject ;     -- The subject of an instruction
    Relation ;    -- A verb phrase
    Object ;      -- An object

  fun
    MkInstruction : Subject -> Relation -> Instruction ;
    People : Subject ;
    Know : Object -> Relation ;
    Java : Object ;
}

