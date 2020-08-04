BatacaPattern {
     var <>kick, <>sn, <>ch, <>oh, <>rim, <>cym, <>bell, <>clap, <>sh, <>ht, <>mt, <>lt, <>acc;

    *new { arg kick=[], sn=[], ch=[], oh=[], rim=[], cym=[], bell=[], clap=[], sh=[], ht=[], mt=[], lt=[], acc=[];
		// this.kick = BatacaPattern.asArray(kick);
		// this.debug("kick");
		// this.debug("this");
		// kick.debug("kick");
		// BatacaPattern.asArray(kick).debug("kick pat");
		^super.newCopyArgs(
            BatacaPattern.asArray(kick),
            BatacaPattern.asArray(sn),
            BatacaPattern.asArray(ch),
            BatacaPattern.asArray(oh),
            BatacaPattern.asArray(rim),
            BatacaPattern.asArray(cym),
            BatacaPattern.asArray(bell),
            BatacaPattern.asArray(clap),
            BatacaPattern.asArray(sh),
            BatacaPattern.asArray(ht),
            BatacaPattern.asArray(mt),
            BatacaPattern.asArray(lt),
            BatacaPattern.asArray(lt),
		)
    }

    // converts an array of integers to an array of 1's and \r's
    // \param size Size of the array
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

Bataca {
	classvar <all;
	var kick, sn, ch, oh, rim, cym, bell, clap, sh, ht, mt, lt;


    // use 
    // ~bataca = Bataca.players;
    // Tocata.sample(\kick, ~kicksamplearray), ...
    players { arg kick, sn, ch, oh, rim, cym, bell, clap, sh, ht, mt, lt;
		^super.new(
            kick:kick,
            sn:  sn,
            ch:  ch,
            oh:  oh,
            rim: rim,
            cym: cym,
            bell:bell,
            clap:clap,
            sh:  sh,
            ht:  ht,
            mt:  mt,
            lt:  lt,
			// kick: Tocata.sample(\kick, kick, kickchannels),
			// sn: Tocata.sample(\sn, sn, snchannels),
			// ch: Tocata.sample(\ch, ch, chchannels),
			// oh: Tocata.sample(\oh, oh, ohchannels),
			// rim: Tocata.sample(\rim, rim, rimchannels),
			// cym: Tocata.sample(\cym, cym, cymchannels),
			// bell: Tocata.sample(\bell, bell, bellchannels),
			// clap: Tocata.sample(\clap, clap, clapchannels),
			// sh: Tocata.sample(\sh, sh, shchannels),
			// ht: Tocata.sample(\ht, ht, htchannels),
			// mt: Tocata.sample(\mt, mt, mtchannels),
			// lt: Tocata.sample(\lt, lt, ltchannels),
		)

		// Bataca.all.do{|it| it.dur_(1/4).legato_(2)};
		// Bataca.quant;
    }

    *quant { arg quant = 16;
        Bataca.all.do{|it|
            // it.quant_(quant);
            it.class.postln;
        };
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

    *samples {
        ^[~kick, ~sn, ~ch, ~oh, ~rim, ~cym, ~bell, ~clap, ~sh, ~ht, ~mt, ~lt];
	}
	
	*names {
		^all.keys.asArray.sort;
	}

	*directory {
		^this.names.collect(_.postln);
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

    pattern { arg patternname;
        "----- TODO!!!".postln;
        // var pat = patternname;
        // sounds[\kick].set(\rhythm, pat.kick.pseq(inf));
        // sounds[\sn].set(\rhythm, pat.sn.pseq(inf));
        // sounds[\ch].set(\rhythm, pat.ch.pseq(inf));
        // sounds[\oh].set(\rhythm, pat.oh.pseq(inf));
        // sounds[\rim].set(\rhythm, pat.rim.pseq(inf));
        // sounds[\cym].set(\rhythm, pat.cym.pseq(inf));
        // sounds[\bell].set(\rhythm, pat.bell.pseq(inf));
        // sounds[\clap].set(\rhythm, pat.clap.pseq(inf));
        // sounds[\sh].set(\rhythm, pat.sh.pseq(inf));
        // sounds[\ht].set(\rhythm, pat.ht.pseq(inf));
        // sounds[\mt].set(\rhythm, pat.mt.pseq(inf));
        // sounds[\lt].set(\rhythm, pat.lt.pseq(inf));
        // ^pat;
    }
}

+ Bataca {

	*initClass {
		all = IdentityDictionary[
            // basic
        \basic -> BatacaPattern(
            kick: [1,7],
            sn: [5,13],
        ),
        \boots -> BatacaPattern(
            kick: [1,9],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \tinyhouse -> BatacaPattern(
            kick: [1,5,9,13],
            oh: [3,7,11,15],
        ),
        \goodtogo -> BatacaPattern(
            kick: [1,4,7,11],
            sn: [5,13],
        ),
        \hiphop -> BatacaPattern(
            kick: [1,3,7,8,15],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        // standard breaks
        \stdbreak1 -> BatacaPattern(
            kick: [1,11],
            sn: [5,13],
            ch: [1,3,5,7,9,10,11,13,15],
        ),
        \stdbreak2 -> BatacaPattern(
            kick: [1,11],
            sn: [5,13],
            ch: [1,3,5,7,8,9,11,15],
        ),
        \rollingbreak -> BatacaPattern(
            kick: [1,8,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        \unknowndrummer -> BatacaPattern(
            acc: [5,13],
            kick: [1,8,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        // rock
        \rock1 -> BatacaPattern(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
            cy: [1],
        ),
        \rock2 -> BatacaPattern(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
        ),
        \rock3 -> BatacaPattern(
            kick: [1,8,9,11],
            sn: [5,13],
            ch: [1,3,5,7,9,11,15],
            oh: [15],
        ),
        \rock4 -> BatacaPattern(
            kick: [1,8,9,11],
            sn: [5,13,15,16],
            ch: [1,3,5,7,9,11,15],
            oh: [15],
        ),
        // electro
        \electro1a -> BatacaPattern(
            kick: [1,7],
            sn: [5,13],
        ),
        \electro1b -> BatacaPattern(
            kick: [1,7,11,15],
            sn: [5,13],
        ),
        \electro2a -> BatacaPattern(
            kick: [1,7],
            sn: [5,13],
        ),
        \electro2b -> BatacaPattern(
            kick: [1,11,14],
            sn: [5,13],
        ),
        \electro3a -> BatacaPattern(
            kick: [1,7,12],
            sn: [5,13],
        ),
        \electro3b -> BatacaPattern(
            kick: [1,7,12,14],
            sn: [5,13],
        ),
        \electro4 -> BatacaPattern(
            kick: [1,7,11,14],
            sn: [5,13],
        ),
        \siberiannights -> BatacaPattern(
            kick: [1,7],
            sn: [5,13],
            ch: [1,3,4,5,7,8,9,11,12,13,15,16],
        ),
        \newwave -> BatacaPattern(
            kick: [1,7,9,10],
            sn: [5,13],
            ch: (1..16),
            oh: [3],
            sh: [5,13],
        ),
        // house
        \house -> BatacaPattern(
            kick: [1,5,9,13],
            sn: [5,13],
            oh: [3,7,11,13],
            cym: [1],
        ),
        \house2 -> BatacaPattern(
            kick: [1,5,9,13],
            sn: [5,13],
            ch: (1..16),
            oh: [3,6,11,14],
        ),
        \brithouse -> BatacaPattern(
            kick: [1,5,9,13],
            ch: [1,2,4,5,6,8,9,10,12,13,14,16],
            oh: [3,6,11,15],
            clap: [5,13],
            cym: [3,7,11,15],
        ),
        \frenchhouse -> BatacaPattern(
            kick: [1,5,9,13],
            ch: (1..16),
            oh: [2,4,6,8,10,12,14,16],
            clap: [5,13],
            sh: [1,2,3,5,7,8,9,10,11,13,15,16],
        ),
        \dirtyhouse -> BatacaPattern(
            acc: [3,16],
            kick: [1,3,5,9,11,13,16],
            sn: [5,13],
            ch: [11,16],
            oh: [3,11,15],
            clap: [3,5,9,11,13],
        ),
        \deephouse -> BatacaPattern(
            kick: [1,5,9,13],
            ch: [2,8,10],
            oh: [3,7,11,15],
            clap: [5,13],
        ),
        \deeperhouse -> BatacaPattern(
            kick: [1,5,9,13],
            oh: [3,7,11,12,15],
            clap: [2,10],
            sh: [4,9],
            mt: [3,8,11],
        ),
        \slowdeephouse -> BatacaPattern(
            kick: [1,5,9,13],
            ch: [1,5,9,13],
            oh: [3,4,7,8,10,11,13],
            clap: [5,13],
            sh: (1..16),
        ),
        \footworka -> BatacaPattern(
            kick: [1,4,7,9,12,15],
            ch: [3,11],
            rim: (1..16),
            clap: [13],
        ),
        \footworkb -> BatacaPattern(
            kick: [1,4,7,9,12,15],
            ch: [3,7,8,11,15],
            rim: (1..16),
            clap: [13],
        ),
        // funk
        \amena -> BatacaPattern(
            kick: [1,3,11,12],
            sn: [5,8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \amenb -> BatacaPattern(
            kick: [1,3,11,12],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [5],
        ),
        \amenc -> BatacaPattern(
            kick: [1,3,11],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [15],
        ),
        \amend -> BatacaPattern(
            kick: [1,3,11],
            sn: [2,5,8,10,15],
            ch: [1,3,5,7,9,13,15],
            cym: [11],
        ),
        \funky -> BatacaPattern(
            kick: [1,3,7,11,14],
            sn: [5,8,10,12,13,16],
            ch: [1,2,3,4,5,6,7,9,10,11,12,13,15,16],
            oh: [8,14],
        ),
        \impeach -> BatacaPattern(
            kick: [1,8,9,15],
            sn: [5,13],
            ch: [1,3,5,7,8,9,13,15],
            oh: [11],
        ),
        \levee -> BatacaPattern(
            kick: [1,2,8,11,12],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \newday -> BatacaPattern(
            kick: [1,3,8,11,12,16],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ),
        \bigbeat -> BatacaPattern(
            kick: [1,4,7,9],
            sn: [5,13],
            ch: [5,13],
        ),
        \ashley -> BatacaPattern(
            kick: [1,3,7,9,10],
            sn: [5,13],
            ch: [1,3,5,7,9,13,15],
            oh: [11],
            bell: [1,3,5,7,9,11,13,15],
        ),
        \papa -> BatacaPattern(
            kick: [1,8,9,11,16],
            sn: [5,13],
            ch: [5,9,11,13,15,16],
            cym: [5],
        ),
        \superstition -> BatacaPattern(
            kick: [1,5,9,13],
            sn: [5,13],
            ch: [1,3,5,7,8,9,10,11,13,15,16],
        ),
        // template
        // \patternname -> BatacaPattern(
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
