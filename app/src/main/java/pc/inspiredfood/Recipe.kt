package pc.inspiredfood

data class Recipe(val id: Int, var name: String, var category: Int, var instructions: String,
                  var popularity: Int, var noOfPeople: Int) {
}