(FUNCTION  gcd  [(int u) (int v)]
  (BB 2
    (OPER 3 Func_Entry []  [])
  )
  (BB 3
    (OPER 4 EQ [(r 4)]  [(r 2)(i 0)])
    (OPER 5 BEQ []  [(r 4)(i 0)(bb 6)])
  )
  (BB 4
    (OPER 6 Mov [(m RetReg)]  [(r 1)])
    (OPER 7 Jmp []  [(bb 1)])
  )
  (BB 5
  )
  (BB 1
    (OPER 1 Func_Exit []  [])
    (OPER 2 Return []  [(m RetReg)])
  )
  (BB 6
    (OPER 8 Div_I [(r 5)]  [(r 1)(r 2)])
    (OPER 9 Mul_I [(r 6)]  [(r 5)(r 2)])
    (OPER 10 Sub_I [(r 7)]  [(r 1)(r 6)])
    (OPER 11 Pass []  [(r 7)])
    (OPER 12 Pass []  [(r 2)])
    (OPER 13 JSR []  [(s gcd)])
    (OPER 14 Mov [(r 8)]  [(m RetReg)])
    (OPER 15 Mov [(m RetReg)]  [(r 8)])
    (OPER 16 Jmp []  [(bb 1)])
    (OPER 17 Jmp []  [(bb 5)])
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
    (OPER 10 Pass []  [(r 3)])
    (OPER 11 Pass []  [(r 1)])
    (OPER 12 JSR []  [(s gcd)])
    (OPER 13 Mov [(r 5)]  [(m RetReg)])
    (OPER 14 Pass []  [(r 5)])
    (OPER 15 JSR []  [(s output)])
    (OPER 16 Mov [(r 6)]  [(m RetReg)])
  )
  (BB 1
    (OPER 1 Func_Exit []  [])
    (OPER 2 Return []  [(m RetReg)])
  )
)
