# Strip all java files in current directory
# Look for *.java.
# For each file, save a copy as basname.save,
# then output to basename.out, and copy over original file, basename.java
# 
use File::Copy;

#$BIN="c:/data/dev/bin";
$BIN=$ARGV[0]; 
$javafile=$ARGV[1]; 


#opendir(DIR, $javadir);
#@files = grep(/\.java$/,readdir(DIR));
#closedir(DIR);

    #$file = $javadir . "/" . $file;
    $file = $javafile;
    print "$file\n";

    #determine output file
    @tmp = split(/\./, $file);
    $basename=$tmp[0];
    $outfile = $basename . ".out";
    $savefile = $basename . ".save";
    copy ($file, $savefile);

    # process file
    system "perl $BIN/striplog_thorough.pl $file $outfile"; 

    system "rm -f $file"; 
    system "mv $outfile $file"; 
