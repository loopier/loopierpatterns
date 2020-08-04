Bataca {
	classvar <all;
    var <>kick, <>sn, <>ch, <>oh, <>rim, <>cym, <>bell, <>clap, <>sh, <>ht, <>mt, <>lt, <>acc;

    *new { arg kick=[], sn=[], ch=[], oh=[], rim=[], cym=[], bell=[], clap=[], sh=[], ht=[], mt=[], lt=[], acc=[];
		^super.newCopyArgs(
            Bataca.asArray(kick),
            Bataca.asArray(sn),
            Bataca.asArray(ch),
            Bataca.asArray(oh),
            Bataca.asArray(rim),
            Bataca.asArray(cym),
            Bataca.asArray(bell),
            Bataca.asArray(clap),
            Bataca.asArray(sh),
            Bataca.asArray(ht),
            Bataca.asArray(mt),
            Bataca.asArray(lt),
            Bataca.asArray(lt),
		)
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
        selector.debug("selector");
        args.debug("args");
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
}

BatacaPlayer {
	// var kick, sn, ch, oh, rim, cym, bell, clap, sh, ht, mt, lt;
    var <>sounds;

    *new { arg kick, sn, ch, oh, rim, cym, bell, clap, sh, ht, mt, lt;
        var sounds = (
            \kick: kick,
            \sn: sn,
            \ch: ch,
            \oh: oh,
            \rim: rim,
            \cym: cym,
            \bell: bell,
            \clap: clap,
            \sh: sh,
            \ht: ht,
            \mt: mt,
            \lt: lt,
        );
        ^super.newCopyArgs(sounds);
    }

	play {
        // plays the Ndef
        sounds.do{|it| it.play};
    }

	stop {
        // stops the Tocata inside the Ndef (see Tocata.stop
        // -- actually PLbinefEnvironment.stop overriding)
        // It's done like this because if we stop the Ndef all
        // effects will be stopped immediately and we want them
        // to keep going until they are done (e.g. reverb)
        sounds.do{|it| it.source.stop};
    }

    pattern { arg bataca;
        // "----- TODO!!!".postln;
        sounds.kick.rhythm_(bataca.kick.pseq(inf));
        sounds.sn.rhythm_(bataca.sn.pseq(inf));
        sounds.ch.rhythm_(bataca.ch.pseq(inf));
        sounds.oh.rhythm_(bataca.oh.pseq(inf));
        sounds.rim.rhythm_(bataca.rim.pseq(inf));
        sounds.cym.rhythm_(bataca.cym.pseq(inf));
        sounds.bell.rhythm_(bataca.bell.pseq(inf));
        sounds.clap.rhythm_(bataca.clap.pseq(inf));
        sounds.sh.rhythm_(bataca.sh.pseq(inf));
        sounds.ht.rhythm_(bataca.ht.pseq(inf));
        sounds.mt.rhythm_(bataca.mt.pseq(inf));
        sounds.lt.rhythm_(bataca.lt.pseq(inf));
        ^bataca;
    }
}

+ Bataca {

	*initClass {
		all = IdentityDictionary[
            // basic
        \basic -> Bataca(
            kick: [1,7],
            sn: [5,13],
        ),
        \boots -> Bataca(
            kick: [1,9],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \tinyhouse -> Bataca(
            kick: [1,5,9,13],
            oh: [3,7,11,15],
        ),
        \goodtogo -> Bataca(
            kick: [1,4,7,11],
            sn: [5,13],
        ),
        \hiphop -> Bataca(
            kick: [1,3,7,8,15],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        // standard breaks
        \stdbreak1 -> Bataca(
            kick: [1,11],
            sn: [5,13],
            ch: [1,3,5,7,9,10,11,13,15],
        ),
        \stdbreak2 -> Bataca(
            kick: [1,11],
            sn: [5,13],
            ch: [1,3,5,7,8,9,11,15],
        ),
        \rollingbreak -> Bataca(
            kick: [1,8,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        \unknowndrummer -> Bataca(
            acc: [5,13],
            kick: [1,8,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        // rock
        \rock1 -> Bataca(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
            cy: [1],
        ),
        \rock2 -> Bataca(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        \rock3 -> Bataca(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
            oh: [15],
        ),
        \rock4 -> Bataca(
            kick: [1,8,9,11],
            sn: [5,13,15,16],
            ch: [1,3,5,7,9,11,15],
            oh: [15],
        ),
        // electro
        \electro1a -> Bataca(
            kick: [1,7],
            sn: [5,13],
        ),
        \electro1b -> Bataca(
            kick: [1,7,11,15],
            sn: [5,13],
        ),
        \electro2a -> Bataca(
            kick: [1,7],
            sn: [5,13],
        ),
        \electro2b -> Bataca(
            kick: [1,11,14],
            sn: [5,13],
        ),
        \electro3a -> Bataca(
            kick: [1,7,12],
            sn: [5,13],
        ),
        \electro3b -> Bataca(
            kick: [1,7,12,14],
            sn: [5,13],
        ),
        \electro4 -> Bataca(
            kick: [1,7,11,14],
            sn: [5,13],
        ),
        \siberiannights -> Bataca(
            kick: [1,7],
            sn: [5,13],
            ch: [1,3,4,5,7,8,9,11,12,13,15,16],
        ),
        \newwave -> Bataca(
            kick: [1,7,9,10],
            sn: [5,13],
            ch: (1..16),
            oh: [3],
            sh: [5,13],
        ),
        // house
        \house -> Bataca(
            kick: [1,5,9,13],
            sn: [5,13],
            oh: [3,7,11,13],
            cym: [1],
        ),
        \house2 -> Bataca(
            kick: [1,5,9,13],
            sn: [5,13],
            ch: (1..16),
            oh: [3,6,11,14],
        ),
        \brithouse -> Bataca(
            kick: [1,5,9,13],
            ch: [1,2,4,5,6,8,9,10,12,13,14,16],
            oh: [3,6,11,15],
            clap: [5,13],
            cym: [3,7,11,15],
        ),
        \frenchhouse -> Bataca(
            kick: [1,5,9,13],
            ch: (1..16),
            oh: [2,4,6,8,10,12,14,16],
            clap: [5,13],
            sh: [1,2,3,5,7,8,9,10,11,13,15,16],
        ),
        \dirtyhouse -> Bataca(
            acc: [3,16],
            kick: [1,3,5,9,11,13,16],
            sn: [5,13],
            ch: [11,16],
            oh: [3,11,15],
            clap: [3,5,9,11,13],
        ),
        \deephouse -> Bataca(
            kick: [1,5,9,13],
            ch: [2,8,10],
            oh: [3,7,11,15],
            clap: [5,13],
        ),
        \deeperhouse -> Bataca(
            kick: [1,5,9,13],
            oh: [3,7,11,12,15],
            clap: [2,10],
            sh: [4,9],
            mt: [3,8,11],
        ),
        \slowdeephouse -> Bataca(
            kick: [1,5,9,13],
            ch: [1,5,9,13],
            oh: [3,4,7,8,10,11,13],
            clap: [5,13],
            sh: (1..16),
        ),
        \footworka -> Bataca(
            kick: [1,4,7,9,12,15],
            ch: [3,11],
            rim: (1..16),
            clap: [13],
        ),
        \footworkb -> Bataca(
            kick: [1,4,7,9,12,15],
            ch: [3,7,8,11,15],
            rim: (1..16),
            clap: [13],
        ),
        // funk
        \amena -> Bataca(
            kick: [1,3,11,12],
            sn: [5,8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \amenb -> Bataca(
            kick: [1,3,11,12],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [5],
        ),
        \amenc -> Bataca(
            kick: [1,3,11],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [15],
        ),
        \amend -> Bataca(
            kick: [1,3,11],
            sn: [2,5,8,10,15],
            ch: [1,3,5,7,9,13,15],
            cym: [11],
        ),
        \funky -> Bataca(
            kick: [1,3,7,11,14],
            sn: [5,8,10,12,13,16],
            ch: [1,2,3,4,5,6,7,9,10,11,12,13,15,16],
            oh: [8,14],
        ),
        \impeach -> Bataca(
            kick: [1,8,9,15],
            sn: [5,13],
            ch: [1,3,5,7,8,9,13,15],
            oh: [11],
        ),
        \levee -> Bataca(
            kick: [1,2,8,11,12],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \newday -> Bataca(
            kick: [1,3,8,11,12,16],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \bigbeat -> Bataca(
            kick: [1,4,7,9],
            sn: [5,13],
            ch: [5,13],
        ),
        \ashley -> Bataca(
            kick: [1,3,7,9,10],
            sn: [5,13],
            ch: [1,3,5,7,9,13,15],
            oh: [11],
            bell: [1,3,5,7,9,11,13,15],
        ),
        \papa -> Bataca(
            kick: [1,8,9,11,16],
            sn: [5,13],
            ch: [5,9,11,13,15,16],
            cym: [5],
        ),
        \superstition -> Bataca(
            kick: [1,5,9,13],
            sn: [5,13],
            ch: [1,3,5,7,8,9,10,11,13,15,16],
        ),
        // template
        // \patternname -> Bataca(
        //     kick: [0],
        //     sn: [0],
        //     ch: [0],
        //     oh: [0],
        //     rim: [0],
        //     cym: [0],
        //     bell: [0],
        //     clap: [0],
        //     sh: [0],
        //     ht: [0],
        //     mt: [0],
        //     lt: [0],
        // ),
		];

		// all = all.freezeAsParent;
	}
}
