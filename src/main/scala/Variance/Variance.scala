package Variance

class PersonWrapperCo[+T](val person: T)
class PersonWrapperContra[-T](person: T)

class Father
class Child extends Father

class PersonWrapperContainerCo(val personWrapperCo: PersonWrapperCo[Father])
class PersonWrapperContainerContra(val personWrapperContra: PersonWrapperContra[Father])