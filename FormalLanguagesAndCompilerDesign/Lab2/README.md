### Finite Automata file
* JSON file
* Only one object with a fields called __states__
* Each state is an object containing
    * An __index__ (starting from 0)
    * A fields __isFinal__ which tells us if the state is final
    * __transitions__, an object containing all the transitions from
    this state to another. Each tranistions is an object
    containing:
        * A field __with__ which is a list with all the elements that 
        can be used to transition to the next state
        * A field __to__ which is the index of the next state