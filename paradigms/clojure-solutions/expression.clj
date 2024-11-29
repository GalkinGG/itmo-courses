(load-file "proto.clj")

(defn createOp [f]
  (fn [& args]
    (fn [vars]
      (apply f (map #(% vars) args)))))


(defn constant [value] (constantly value))

(defn variable [var] #(% var))

(def add (createOp +))
(def subtract (createOp -))
(def multiply (createOp *))
(def divide (createOp #(/ (double %1) %2)))
(def negate subtract)
(def exp (createOp #(Math/exp %)))
(def ln (createOp #(Math/log %)))

(def FUNC_OPERATIONS
  {'+ add
   '- subtract
   '* multiply
   '/ divide
   'negate negate
   'exp exp
   'ln ln})

(defn Parse [constantFunc variableFunc mapOP]
  (fn [line] (let [expression (read-string (str line))]
    (if (number? expression)
      (constantFunc expression)
      (if (symbol? expression)
        (variableFunc (str expression))
        (apply (mapOP (first expression)) (map (Parse constantFunc variableFunc mapOP) (rest expression))))))))

(def parseFunction (Parse constant variable FUNC_OPERATIONS))


(def toString (method :toString))
(def toStringPostfix (method :toStringPostfix))
(def evaluate (method :evaluate))

(def -val (field :val))

(defn _Constant
  [this val]
  (assoc this
    :val val))

(def ConstantPrototype
  {:toString (fn [this] (str (-val this)))
   :toStringPostfix toString
   :evaluate (fn [this _] (-val this))})

(def Constant (constructor _Constant ConstantPrototype))

(defn getVarKey [var]
  (str (get (clojure.string/lower-case var) 0)))

(def -varName (field :varName))

(defn _Variable
  [this val]
  (assoc this
    :varName val))

(def VariablePrototype
  {:toString (fn [this] (str (-varName this)))
   :toStringPostfix toString
   :evaluate (fn [this args] (args (getVarKey (-varName this))))})

(def Variable (constructor _Variable VariablePrototype))


(def -op (field :op))
(def -eval (field :eval))
(def -args (field :args))

(defn _Operation
  [op eval this & args]
  (assoc this
    :op op
    :eval eval
    :args args))


(def OperationPrototype
  {:toString (fn [this] (str "(" (-op this) " " (clojure.string/join " " (map #(toString %) (-args this))) ")"))
   :toStringPostfix (fn [this] (str "(" (clojure.string/join " " (map #(toStringPostfix %) (-args this))) " " (-op this) ")"))
   :evaluate (fn [this args] (apply (-eval this) (map #(evaluate % args) (-args this))))})


(defn Operation
  [funcSign evalFunc]
  (constructor (partial _Operation funcSign evalFunc) OperationPrototype))


(def Add (Operation "+" +))
(def Subtract (Operation "-" -))
(def Multiply (Operation "*" *))
(def Divide (Operation "/" #(/ (double %1) %2)))
(def Negate (Operation "negate" -))
(def Sin (Operation "sin" #(Math/sin %)))
(def Cos (Operation "cos" #(Math/cos %)))

(def Inc (Operation "++" #(+ % 1)))
(def Dec (Operation "--" #(- % 1)))

(def OBJECT_OPERATIONS
  {'+      Add
   '-      Subtract
   '*      Multiply
   '/      Divide
   'negate Negate
   'sin Sin
   'cos Cos
   '++ Inc
   '-- Dec})

(def parseObject (Parse Constant Variable OBJECT_OPERATIONS))

;~~~~~~~~~~~~~~~~~~Comb parser~~~~~~~~~~~~~~~~~~~~~~

(load-file "parser.clj")

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))

(def *digit (+char "0123456789."))
(defn sign [s tail]
  (if (#{\- \+} s)
    (cons s tail)
    tail))
(def *const (+map #(Constant (read-string %)) (+str (+seqf sign (+opt (+char "-+")) (+plus *digit)))))
(def *var (+map Variable (+str (+seq *ws (+str (+plus (+char "xyzXYZ"))) *ws))))


(def binOperations (+char "+-*/"))
(def unaryOperations (+str (+or (+seq (+char "n") (+char "e") (+char "g") (+char "a") (+char "t") (+char "e"))
                           (+seq (+char "-") (+char "-"))
                           (+seq (+char "+") (+char "+")))))


(def parseObjectPostfix
  (letfn [
          (*expression []
            (+map #(apply (OBJECT_OPERATIONS (symbol (str (last %)))) (butlast %))
                  (+or
                    (+seq *ws (+opt (+ignore (+char "("))) *ws (*value) *ws unaryOperations *ws (+opt (+ignore (+char ")"))) *ws)
                    (+seq *ws (+opt (+ignore (+char "("))) *ws (*value) *ws (*value) *ws binOperations *ws (+opt (+ignore (+char ")"))) *ws))))
          (*value []
            (+or
              *var
              *const
              (delay (*expression))))]
    (+parser (+seqn 0 *ws (delay (*value)) *ws))))
