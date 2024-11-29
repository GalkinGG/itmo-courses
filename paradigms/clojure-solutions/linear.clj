(defn vectorOperation [f]
  (fn [v1, v2] (mapv f v1 v2)))

(def v+ (vectorOperation +))
(def v- (vectorOperation -))
(def v* (vectorOperation *))
(def vd (vectorOperation /))

(defn scalar [v1, v2] (apply + (v* v1 v2)))

(defn vect [v1, v2] (vector (- (* (second v1) (last v2)) (* (last v1) (second v2))),
                            (- (* (last v1) (first v2)) (* (first v1) (last v2))),
                            (- (* (first v1) (second v2)) (* (second v1) (first v2)))))

(defn v*s [v1, s] (mapv (fn [element] (* element s)) v1))

(defn matrixOperation [f] (fn [m1, m2] (mapv (vectorOperation f) m1 m2)))

(def m+ (matrixOperation +))
(def m- (matrixOperation -))
(def m* (matrixOperation *))
(def md (matrixOperation /))

(defn m*s [m, s] (mapv (fn [v] (v*s v s)) m))
(defn m*v [m, v1] (mapv (fn [v] (scalar v v1)) m))
(defn transpose [m] (apply mapv vector m))
(defn m*m [m1, m2]
  (mapv (fn [v] (m*v (transpose m2) v)) m1))

(defn shapelessOp [f, m1, m2] (if
                                (vector? m1) (mapv #(shapelessOp f %1 %2) m1 m2)
                                (f m1 m2)
                                ))

(defn s+ [m1, m2] (shapelessOp + m1 m2))
(defn s- [m1, m2] (shapelessOp - m1 m2))
(defn s* [m1, m2] (shapelessOp * m1 m2))
(defn sd [m1, m2] (shapelessOp / m1 m2))

