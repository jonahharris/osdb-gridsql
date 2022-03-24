$infile = $ARGV[0];

#@tmp = split(/\./, $infile);
#$basename=$tmp[0];
#$outfile = $basename . ".out";

$outfile = $ARGV[1];

open(INPUT, $infile) or die $!;
open(OUTPUT, ">$outfile") or die $!;

$procflag = 0;

while (<INPUT>)
{
	if ($_ =~ /.*private.*static.*final.*XLogger.*logger/) { $procflag = 1; }
	if ($_ =~ /.*logger\.log.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.info.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.warn.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.entering.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.exiting.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.catching.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.throwing.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.debug.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.trace.*/) { $procflag = 1; }
	if ($_ =~ /.*logger\.supertrace.*/) { $procflag = 1; }
	if ($_ =~ /.*CATEGORY_MESSAGE\..*/) { $procflag = 1; }
	if ($_ =~ /.*CATEGORY_LOCK\..*/) { $procflag = 1; }
	if ($_ =~ /.*CATEGORY_QUERYFLOW\..*/) { $procflag = 1; }
	if ($_ =~ /.*final String method =.*/) { $procflag = 1; }
	if ($_ =~ /.*FaultGenerator.generate.*/) { $procflag = 1; }

	if ($_ =~ /.*logger.*/) { $procflag = 1; }
	if ($_ =~ /.*XLogger.*/) { $procflag = 1; }

	# look for ";"
	if ($_ =~ /;\s*$/ && $procflag == 1)
	{
		$procflag = 0;
		next;
	}

	# We also allow //BUILD_CUT_START and //BUILD_CUT_END
	# to appear in the code comments, to indicate other sections of
	# code for removal. This allows us to remove toString and 
	# other debugging methods that may help in reverse engineering.
	if ($_ =~ /BUILD_CUT_START/)
	{
		$procflag = 2;
		next;
	}

	if ($_ =~ /BUILD_CUT_ALT/ && $procflag == 2)
	{
		$procflag = 3;
		next;
	}

	if ($_ =~ /BUILD_CUT_END/ && ($procflag == 2 || $procflag == 3))
	{
		$procflag = 0;
		next;
	}


	# Also, substitute logger.isTraceEnabled() with false
	s/logger.isTraceEnabled../false/g;
	s/logger.isDebugEnabled../false/g;

	if ($procflag == 0)
	{
		print OUTPUT $_;
	}

	if ($procflag == 3)
	{
		s/\/\///g;
		print OUTPUT $_;
	}
}

close (INPUT);
close (OUTPUT);
