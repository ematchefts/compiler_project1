(FUNCTION  gcd  [(int u) (int v)]
  (BB 2
    (OPER 3 Func_Entry []  [])
  )
  (BB 3
    (OPER -1 EQ [(r 4)]  [(r 2)(i 0)])
    (OPER 4 BEQ []  [(r 4)(i 0)(bb 6)])
  )
  (BB 4
    (OPER 5 Mov [(m RetReg)]  [(r 1)])
    (OPER 6 Jmp []  [(bb 1)])
  )
  (BB 5
  )
  (BB 1
    (OPER 1 Func_Exit []  [])
    (OPER 2 Return []  [(m RetReg)])
  )
  (BB 6
    (OPER 7 JSR []  [(s gcd)])
    (OPER 8 Mov [(r 5)]  [(m RetReg)])
    (OPER 9 Mov [(m RetReg)]  [(r 5)])
    (OPER 10 Jmp []  [(bb 1)])
    (OPER 11 Jmp []  [(bb 5)])
  )
)
(FUNCTION  main  []
  (BB 2
    (OPER 3 Func_Entry []  [])
  )
  (BB 3
    (OPER 4 JSR []  [(s input)])
    (OPER 5 Mov [(r 2)]  [(m RetReg)])
    (OPER 6 Mov [(r 1)]  [(r 2)])
    (OPER 7 JSR []  [(s input)])
    (OPER 8 Mov [(r 4)]  [(m RetReg)])
    (OPER 9 Mov [(r 3)]  [(r 4)])
    (OPER 10 JSR []  [(s output)])
    (OPER 11 Mov [(r 5)]  [(m RetReg)])
  )
  (BB 1
    (OPER 1 Func_Exit []  [])
    (OPER 2 Return []  [(m RetReg)])
  )
)
