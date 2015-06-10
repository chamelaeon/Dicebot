package com.chamelaeon.dicebot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.chamelaeon.dicebot.api.Dicebot;
import com.chamelaeon.dicebot.api.HelpDetails;
import com.chamelaeon.dicebot.framework.DicebotGenericEvent;
import com.chamelaeon.dicebot.framework.DicebotListenerAdapter;

/**
 * A command to provide a changelog to the listener.
 * @author Chamelaeon
 */
public class ChangelogCommand extends DicebotListenerAdapter {

    SortedMap<String, List<String>> versionToChangesMap;
    
    public ChangelogCommand() {
        super("!changelog[ ]*([0-9\\.]*)", new HelpDetails("changelog", "Displays the latest changes for the bot. If a version is provided, " 
                + "displays the changes for that version.", Arrays.asList("!changelog", "!changelog 1.2.0")));
        versionToChangesMap = new TreeMap<>();
        
        try (Scanner scanner = new Scanner(this.getClass().getClassLoader().getResourceAsStream("changelog.txt"))) {
            List<String> changesForVersion = null;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();

                if (line.startsWith("#")) {
                    String version = line.substring(1);
                    changesForVersion = new ArrayList<>();
                    versionToChangesMap.put(version, changesForVersion);
                } else {
                    changesForVersion.add(line);
                }
            }
        }
    }
    
    @Override
    public void onSuccess(DicebotGenericEvent<Dicebot> event, List<String> groups) {
        List<String> changes;
        String key = null;
        
        if (groups.size() > 1) {
            key = groups.get(1);
            if (!StringUtils.isEmpty(key) && !versionToChangesMap.containsKey(key)) {
                return;
            }
        }
        
        if (StringUtils.isEmpty(key)) {
            key = versionToChangesMap.lastKey();
        }
        
        changes = versionToChangesMap.get(key);
        event.respond("Changes for version " + key + ":");
        for (String change : changes) {
            event.respond("- " + change);
        }
        event.respond("All available versions: " + StringUtils.join(versionToChangesMap.keySet(), ", "));
    }
}
