package com.odtheking.zcon.commands

import com.github.stivais.commodore.Commodore
import com.github.stivais.commodore.utils.GreedyString
import com.odtheking.odin.utils.modMessage
import com.odtheking.odin.utils.sendCommand

// Commands are handled via https://github.com/Stivais/Commodore
val testcommand = Commodore("oa", "odinaddon") {

    runs {
        modMessage("Odin Addon command executed")
    }

    runs { greedy: GreedyString ->
        modMessage("Command with parameter executed: ${greedy.string}")
    }
}

val maxwell = Commodore("ac"){
    runs{
        sendCommand("call maxwell")
    }
}

val stashItem = Commodore("stash"){
    runs{
        sendCommand("viewstash item")
    }
}

val stashMaterial = Commodore("mstash"){
    runs{
        sendCommand("viewstash material")
    }
}