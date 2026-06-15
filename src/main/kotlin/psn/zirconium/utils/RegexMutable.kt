package psn.zirconium.utils

class RegexMutable(
    var regexString: String,
    var regexType: RegexType,
){
    private var saved: Regex?=null
    val regex: Regex
        get(){
            saved?:load()
            return saved!!
        }
    fun load(){
        saved=Regex(
            when(regexType){
                RegexType.REGEX -> regexString
                RegexType.CONTAINS -> regexString.replace("/[#-.]|[[-^]|[?|{}]/g", "\\$&")
                RegexType.MATCHES -> "^${regexString.replace("/[#-.]|[[-^]|[?|{}]/g", "\\$&")}$"
            }
        )
    }
}
enum class RegexType{
    REGEX,
    MATCHES,
    CONTAINS,
}