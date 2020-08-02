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
    classvar <patterns;

    *init {
        Bataca.initPatterns;
        Bataca.quant;
    }

    *sample { arg kick, sn, ch, oh, rim, cym, bell, clap, sh, ht, mt, lt, kickchannels=2, snchannels=2, chchannels=2, ohchannels=2, rimchannels=2, cymchannels=2, bellchannels=2, clapchannels=2, shchannels=2, htchannels=2, mtchannels=2, ltchannels=2;
        Tocata.sample(\kick, kick, kickchannels);
        Tocata.sample(\sn, sn, snchannels);
        Tocata.sample(\ch, ch, chchannels);
        Tocata.sample(\oh, oh, ohchannels);
        Tocata.sample(\rim, rim, rimchannels);
        Tocata.sample(\cym, cym, cymchannels);
        Tocata.sample(\bell, bell, bellchannels);
        Tocata.sample(\clap, clap, clapchannels);
        Tocata.sample(\sh, sh, shchannels);
        Tocata.sample(\ht, ht, htchannels);
        Tocata.sample(\mt, mt, mtchannels);
        Tocata.sample(\lt, lt, ltchannels);

		Bataca.all.do{|it| it.dur_(1/4).legato_(2)};
    }

    *channels { arg kick, sn, ch, oh, rim, cym, bell, clap, sh, ht, mt, lt;
    }

    *quant { arg quant = 16;
        Bataca.all.do{|it|
            // it.quant_(quant);
            it.class.postln;
        };
    }

	*play {
        Bataca.all.do{|it| it.play};
    }

	*stop {
        Bataca.all.do{|it| it.stop};
    }

    *all {
        ^[~kick, ~sn, ~ch, ~oh, ~rim, ~cym, ~bell, ~clap, ~sh, ~ht, ~mt, ~lt];
	}

    *pattern { arg name;
		var pat = patterns.at(name);
		~kick.rhythm_(pat.kick.pseq(inf));
		~sn.rhythm_(pat.sn.pseq(inf));
		~ch.rhythm_(pat.ch.pseq(inf));
		~oh.rhythm_(pat.oh.pseq(inf));
		~rim.rhythm_(pat.rim.pseq(inf));
		~cym.rhythm_(pat.cym.pseq(inf));
		~bell.rhythm_(pat.bell.pseq(inf));
		~clap.rhythm_(pat.clap.pseq(inf));
		~sh.rhythm_(pat.sh.pseq(inf));
		~ht.rhythm_(pat.ht.pseq(inf));
		~mt.rhythm_(pat.mt.pseq(inf));
		~lt.rhythm_(pat.lt.pseq(inf));
        ^pat;
    }

	*names {
		^Bataca.patterns.keys.asArray.sort;
	}

	*directory {
		Bataca.names.collect(_.postln);
	}

    *initPatterns {
        patterns = Dictionary.new;
        patterns.put("amena", BatacaPattern(
            kick: [1,3,11,12],
            sn: [5,8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
        ));
        patterns.put("amenb", BatacaPattern(
            kick: [1,3,11,12],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [5],
        ));
        patterns.put("amenc", BatacaPattern(
            kick: [1,3,11],
            sn: [8,10,13,16],
            ch: [1,3,5,7,9,11,13,15],
            rim: [15],
        ));
        patterns.put("amend", BatacaPattern(
            kick: [1,3,11],
            sn: [2,5,8,10,15],
            ch: [1,3,5,7,9,13,15],
            cym: [11],
        ));
        patterns.put("funky", BatacaPattern(
            kick: [1,3,7,11,14],
            sn: [5,8,10,12,13,16],
            ch: [1,2,3,4,5,6,7,9,10,11,12,13,15,16],
            oh: [8,14],
        ));
        patterns.put("impeach", BatacaPattern(
            kick: [1,8,9,15],
            sn: [5,13],
            ch: [1,3,5,7,8,9,13,15],
            oh: [11],
        ));
        patterns.put("levee", BatacaPattern(
            kick: [1,2,8,11,12],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ));
        patterns.put("newday", BatacaPattern(
            kick: [1,3,8,11,12,16],
            sn: [5,13],
            ch: [1,3,5,7,9,11,13,15],
        ));
        patterns.put("bigbeat", BatacaPattern(
            kick: [1,4,7,9],
            sn: [5,13],
            ch: [5,13],
        ));
        patterns.put("ashley", BatacaPattern(
            kick: [1,3,7,9,10],
            sn: [5,13],
            ch: [1,3,5,7,9,13,15],
            oh: [11],
            bell: [1,3,5,7,9,11,13,15],
        ));
        patterns.put("papa", BatacaPattern(
            kick: [1,8,9,11,16],
            sn: [5,13],
            ch: [5,9,11,13,15,16],
            cym: [5],
        ));
        patterns.put("superstition", BatacaPattern(
            kick: [1,5,9,13],
            sn: [5,13],
            ch: [1,3,5,7,8,9,10,11,13,15,16],
        ));
        // template
        // patterns.put("patternname", BatacaPattern(
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
        // ));
    }

}
