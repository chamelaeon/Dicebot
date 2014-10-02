package com.chamelaeon.dicebot.personality;




public class BasicPersonality extends AbstractPersonality {
	// TODO: Fix this class.
	public BasicPersonality() {
		outputTexts.put("LessThanOneGroup", "Rolling fewer than 1 groups may cause pan-dimensional collapse and a great deal of wibbly. Let's not do it.");
		outputTexts.put("Roll0Dice", "It's come to my attention you're rolling 0 dice. I'm pleased to inform you that you've rolled NaN. Good day.");
		outputTexts.put("Roll0Sides", "My god, man, you've rolled a die with no sides! We have to act quickly! You stay put and hold this umbrella, I'll go get the lemurs.");
		outputTexts.put("OneSidedDice", "Well, you rolled %d one-sided dice, so you get a %d. Also, I've been authorized to begin unethical testing with your corpse. Stand by...");
		outputTexts.put("KeepingLessThan1", "Keeping fewer than 1 dice isn't Zen, Dragon-san, it's madness. MADNESS, I TELL YOU!");
		outputTexts.put("RollLessThanKeep", "Rolling fewer dice than you're keeping? This is the perfect time to try out my new anti-cheating Bushido Bot!");
		outputTexts.put("BadCommand", "Somehow, in the milliseconds between asking me to do something and now, I've forgotten how to do the thing you asked! I've even forgotten what it was!");
		outputTexts.put("CannotSatisfyRerollSingleDie", "It seems that the reroll condition %d can't be satisfied by a die with %d sides. I suggest a rousing game of Candyland instead.");
		outputTexts.put("CannotSatisfyRerollMultipleDice", "It seems that the reroll condition %d can't be satisfied by %d dice with %d sides. I suggest a rousing game of Candyland instead.");
		outputTexts.put("InfiniteExplosion", "Make a reservation at Milliways, since the infinitely exploding dice on that roll won't be done until the end of the universe.");
		outputTexts.put("BrokenRegexp", "What's that? You've somehow broken regexps? Oh dear. I think you need to spend some time in the Alone Room.");
		outputTexts.put("ParseBadShort", "Hm. I could handle %s as a value but it would require a fundamental reordering of the universe. Hang on just an eon...");
		outputTexts.put("ReflectionError", "The fundamental constants of the universe seem a little off-kilter today. Better not try that again.");
		outputTexts.put("CannotSatisfySuccesses", "I may be clinically insane, but even I know you'll never get %d successes off %d dice.");
		outputTexts.put("Cheat", " Cheating's limited to the Europa Team only, sorry.");
		
		outputTexts.put("Standard1Group", "rolls %s for %s and gets a natural %s for a result of %s.");
		outputTexts.put("Standard1GroupCrit", "rolls %s for %s and gets a natural (%s), a CRITICAL %s! \"%s\"");
		outputTexts.put("StandardMoreGroups", "rolls %s for %s and gets a natural ( %s) for a result of ( %s).");
		outputTexts.put("L5ROneGroup", "rolls %s for %s and gets %s for a total of %s.");
		outputTexts.put("L5RMoreGroups", "rolls %s for %s and gets ( %s) for totals of ( %s).");
		outputTexts.put("WhiteWolfSuccess", "rolls %s for %s and gets %s for a total of %d successes over minimum.");
		outputTexts.put("WhiteWolfFailure", "rolls %s for %s and gets %s, which is a failure. Would you like a cup of tea instead?");
		outputTexts.put("WhiteWolfBotch", "rolls %s for %s and gets %s, which is a botch!. Shame, really, so much good could be done with those organs...");
		outputTexts.put("Fudge1Group", "rolls %s for %s and gets %s for a result of %s.");
		outputTexts.put("FudgeMoreGroups", "rolls %s for %s and gets ( %s) for results of ( %s).");
		
		criticalFailures.add("Excellent! We've been looking for a test subject. Hold on to this horseshoe and penny while I alert the Leprechaun Entrapment Brigade, we'll have you fixed in no time...");
		criticalFailures.add("We've developed an implantable luck generator you could try, if you aren't using that liver...");
		
		criticalSuccesses.add("Ah, then this IS the reality where you're kicking ass. My apologies, I'll amend my notes.");
		criticalSuccesses.add("Astonishing! May I borrow your pituitary gland for a moment? You'll get it back good as new, I promise!");
	}
}
