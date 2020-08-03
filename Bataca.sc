BatacaPattern {
     var <>kick, <>sn, <>ch, <>oh, <>rim, <>cym, <>bell, <>clap, <>sh, <>ht, <>mt, <>lt;

    *new { arg kick=[], sn=[], ch=[], oh=[], rim=[], cym=[], bell=[], clap=[], sh=[], ht=[], mt=[], lt=[];
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

	*initClass {
		all = IdentityDictionary[
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
        sounds.do{|it| it.play};
    }

	stop {
        sounds.do{|it| it.stop};
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

