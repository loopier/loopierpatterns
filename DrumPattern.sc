DrumPattern {
	classvar <all;
	// var <>kick, <>sn, <>ch, <>oh, <>rim, <>cym, <>bell, <>cl, <>sh, <>ht, <>mt, <>lt, <>acc;
    var <>pattern;

    *new { arg kick=[], sn=[], ch=[], oh=[], rim=[], cym=[], bell=[], cl=[], sh=[], ht=[], mt=[], lt=[], acc=[], size=16;
		var pat = (
            kick: DrumPattern.asArray(kick, size),
            sn: DrumPattern.asArray(sn, size),
            ch: DrumPattern.asArray(ch, size),
            oh: DrumPattern.asArray(oh, size),
            rim: DrumPattern.asArray(rim, size),
            cym: DrumPattern.asArray(cym, size),
            bell: DrumPattern.asArray(bell, size),
            cl: DrumPattern.asArray(cl, size),
            sh: DrumPattern.asArray(sh, size),
            ht: DrumPattern.asArray(ht, size),
            mt: DrumPattern.asArray(mt, size),
            lt: DrumPattern.asArray(lt, size),
		);
		^super.newCopyArgs(pat);
    }

	*at { |key|
		^all.at(key)
	}

	*newFromKey { |key|
		var pattern = this.at(key).deepCopy;
		pattern ?? { ("Unknown pattern " ++ key.asString).warn; ^nil };
		^pattern;
	}

	*doesNotUnderstand { |selector, args|
		var pattern = this.newFromKey(selector, args).deepCopy;
		^pattern ?? { super.doesNotUnderstand(selector, args) };
	}

	*names {
		^all.keys.asArray.sort;
	}

	*directory {
		^this.names.collect(_.postln);
	}

	// returns array of given 'size' with default
	// values set to 'defaultValue', and values at
	// 'postitions' set to 'replaceValue'
    *asArray { arg positions, size=16, zeroIndex=false, defaultValue=\r, replaceValue=1;
        var arr = Array.fill(size, defaultValue);
		// zeroIndex.debug("zero");
        positions.do{|x|
			arr.put(x.asInt - zeroIndex.not.asInteger, replaceValue);
			// x.postln;
        }
        ^arr;
    }

	newFromKey { |key|
		var pattern = this.pattern.at(key).deepCopy;
		pattern ?? { ("Unknown pattern " ++ key.asString).warn; ^nil };
		^pattern;
	}

	doesNotUnderstand { |selector, args|
		var pattern = this.newFromKey(selector, args).deepCopy;
		^pattern ?? { super.doesNotUnderstand(selector, args) };
	}

	at { arg key;
		^pattern.at(key);
	}

// the patterns

	*initClass {
		all = IdentityDictionary[
            // basic
        \basic -> DrumPattern(
            kick: [1,7],
            sn: [5,13],
        ),
        \boots -> DrumPattern(
            kick: [1,9],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \tinyhouse -> DrumPattern(
            kick: [1,5,9,13],
            oh: [3,7,11,15],
        ),
        \goodtogo -> DrumPattern(
            kick: [1,4,7,11],
            sn: [5,13],
        ),
        \hiphop -> DrumPattern(
            kick: [1,3,7,8,15],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        // standard breaks
        \stdbreak1 -> DrumPattern(
            kick: [1,11],
            sn: [5,13],
            ch: [1,3,5,7,9,10,11,13,15],
        ),
        \stdbreak2 -> DrumPattern(
            kick: [1,11],
            sn: [5,13],
            ch: [1,3,5,7,8,9,11,15],
        ),
        \rollingbreak -> DrumPattern(
            kick: [1,8,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        \unknowndrummer -> DrumPattern(
            acc: [5,13],
            kick: [1,8,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        // rock
        \rock1 -> DrumPattern(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
            cy: [1],
        ),
        \rock2 -> DrumPattern(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        \rock3 -> DrumPattern(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
            oh: [15],
        ),
        \rock4 -> DrumPattern(
            kick: [1,8,9,11],
            sn: [5,13,15,16],
            ch: [1,3,5,7,9,11,15],
            oh: [15],
        ),
        // electro
        \electro1a -> DrumPattern(
            kick: [1,7],
            sn: [5,13],
        ),
        \electro1b -> DrumPattern(
            kick: [1,7,11,15],
            sn: [5,13],
        ),
        \electro2a -> DrumPattern(
            kick: [1,7],
            sn: [5,13],
        ),
        \electro2b -> DrumPattern(
            kick: [1,11,14],
            sn: [5,13],
        ),
        \electro3a -> DrumPattern(
            kick: [1,7,12],
            sn: [5,13],
        ),
        \electro3b -> DrumPattern(
            kick: [1,7,12,14],
            sn: [5,13],
        ),
        \electro4 -> DrumPattern(
            kick: [1,7,11,14],
            sn: [5,13],
        ),
        \siberiannights -> DrumPattern(
            kick: [1,7],
            sn: [5,13],
            ch: [1,3,4,5,7,8,9,11,12,13,15,16],
        ),
        \newwave -> DrumPattern(
            kick: [1,7,9,10],
            sn: [5,13],
            ch: (1..16),
            oh: [3],
            sh: [5,13],
        ),
        // house
        \house -> DrumPattern(
            kick: [1,5,9,13],
            sn: [5,13],
            oh: [3,7,11,13],
            cym: [1],
        ),
        \house2 -> DrumPattern(
            kick: [1,5,9,13],
            sn: [5,13],
            ch: (1..16),
            oh: [3,6,11,14],
        ),
        \brithouse -> DrumPattern(
            kick: [1,5,9,13],
            ch: [1,2,4,5,6,8,9,10,12,13,14,16],
            oh: [3,6,11,15],
            cl: [5,13],
            cym: [3,7,11,15],
        ),
        \frenchhouse -> DrumPattern(
            kick: [1,5,9,13],
            ch: (1..16),
            oh: [2,4,6,8,10,12,14,16],
            cl: [5,13],
            sh: [1,2,3,5,7,8,9,10,11,13,15,16],
        ),
        \dirtyhouse -> DrumPattern(
            acc: [3,16],
            kick: [1,3,5,9,11,13,16],
            sn: [5,13],
            ch: [11,16],
            oh: [3,11,15],
            cl: [3,5,9,11,13],
        ),
        \deephouse -> DrumPattern(
            kick: [1,5,9,13],
            ch: [2,8,10],
            oh: [3,7,11,15],
            cl: [5,13],
        ),
        \deeperhouse -> DrumPattern(
            kick: [1,5,9,13],
            oh: [3,7,11,12,15],
            cl: [2,10],
            sh: [4,9],
            mt: [3,8,11],
        ),
        \slowdeephouse -> DrumPattern(
            kick: [1,5,9,13],
            ch: [1,5,9,13],
            oh: [3,4,7,8,10,11,13],
            cl: [5,13],
            sh: (1..16),
        ),
        \footworka -> DrumPattern(
            kick: [1,4,7,9,12,15],
            ch: [3,11],
            rim: (1..16),
            cl: [13],
        ),
        \footworkb -> DrumPattern(
            kick: [1,4,7,9,12,15],
            ch: [3,7,8,11,15],
            rim: (1..16),
            cl: [13],
        ),
        // funk
        \amena -> DrumPattern(
            kick: [1,3,11,12],
            sn: [5,8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \amenb -> DrumPattern(
            kick: [1,3,11,12],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [5],
        ),
        \amenc -> DrumPattern(
            kick: [1,3,11],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [15],
        ),
        \amend -> DrumPattern(
            kick: [1,3,11],
            sn: [2,5,8,10,15],
            ch: [1,3,5,7,9,13,15],
            cym: [11],
        ),
        \funky -> DrumPattern(
            kick: [1,3,7,11,14],
            sn: [5,8,10,12,13,16],
            ch: [1,2,3,4,5,6,7,9,10,11,12,13,15,16],
            oh: [8,14],
        ),
        \impeach -> DrumPattern(
            kick: [1,8,9,15],
            sn: [5,13],
            ch: [1,3,5,7,8,9,13,15],
            oh: [11],
        ),
        \levee -> DrumPattern(
            kick: [1,2,8,11,12],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \newday -> DrumPattern(
            kick: [1,3,8,11,12,16],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \bigbeat -> DrumPattern(
            kick: [1,4,7,9],
            sn: [5,13],
            ch: [5,13],
        ),
        \ashley -> DrumPattern(
            kick: [1,3,7,9,10],
            sn: [5,13],
            ch: [1,3,5,7,9,13,15],
            oh: [11],
            bell: [1,3,5,7,9,11,13,15],
        ),
        \papa -> DrumPattern(
            kick: [1,8,9,11,16],
            sn: [5,13],
            ch: [5,9,11,13,15,16],
            cym: [5],
        ),
        \superstition -> DrumPattern(
            kick: [1,5,9,13],
            sn: [5,13],
            ch: [1,3,5,7,8,9,10,11,13,15,16],
        ),
        \buleria -> DrumPattern(
            kick: [1,5,9],
            ch: [4,9,11],
            oh: [1,7],
            cl: [2,10],
            rim: [5,8,12],
            size: 12,
        )
        // template
        // \patternname -> DrumPattern(
        //     kick: [0],
        //     sn: [0],
        //     ch: [0],
        //     oh: [0],
        //     rim: [0],
        //     cym: [0],
        //     bell: [0],
        //     cl: [0],
        //     sh: [0],
        //     ht: [0],
        //     mt: [0],
        //     lt: [0],
        // ),
		];

		// all = all.freezeAsParent;
	}
}
