<?php
	// Originally based on a script by Wingnut.
		
	// database files
	$count = CONTENT_DIR."counter/hits.txt"; 
	$ipfile = CONTENT_DIR."counter/counterip.txt";
	
	// ip address
		$aktip = $_SERVER['REMOTE_ADDR'];
		if ( ($aktip=="127.0.0.1") && array_key_exists('HTTP_X_FORWARDED_FOR',$_SERVER) && ($_SERVER['HTTP_X_FORWARDED_FOR'] != "" ) ) {
			$aktip = $_SERVER['HTTP_X_FORWARDED_FOR'];
		}
	
	function checkforfiles() {
		global $ipfile, $count;
		// Basically, try to create and/or open the files first
		
		// If 'content' folder does not exist yet, then blog has not been 'initialized'
		if ( file_exists( CONTENT_DIR ) ) {
		
			if ( file_exists( CONTENT_DIR.'counter' ) == false ) {
				sb_create_folder( CONTENT_DIR.'counter' );
			}
			
			if ( file_exists( $count ) == false ) {
				sb_write_file( $count, "" );
				// $filecount = file( $count );
				// $counthandle = @fopen( $filecount,"w");
				// fclose( $counthandle );
			}
				
			if ( file_exists( $ipfile ) == false ) {
				sb_write_file( $ipfile, "" );
				// $file = file( $ipfile );
				// $iphandle = @fopen( $file,"w");
				// fclose( $iphandle );
			}
			
		}
	}
	
	function checkip($ip) {
		global $blog_config, $ipfile;
		
		// duration of IP lock in minutes
		$duration = $blog_config->getTag('BLOG_COUNTER_HOURS');
		
		checkforfiles();
		
		// (ipfile file won't exist if blog has not been 'initialized')
		if ( file_exists( $ipfile ) ) {
			$timestamp = time();
			
			$file = file($ipfile);
			$iphandle = fopen($ipfile,"w+");
			if ( sizeof($file)==0 ) {
				fputs($iphandle, "$ip|$timestamp\n");
				daily_counter();
			} else { 
				foreach ($file as $line) {
					$exp_line = explode("|", $line);
					if (($exp_line[1]+ 60*$duration) < $timestamp) {
						if ($exp_line[0] == $ip) {
							fputs($iphandle, "$exp_line[0]|$timestamp\n");
						} else {
							fputs($iphandle, "$ip|$timestamp\n"); 
						}
						daily_counter();
					} else {
						fputs($iphandle, "$line");
					}
				}
			}
			fclose($iphandle);
		}
	}        
	
	function daily_counter() {
		global $count;
		
		checkforfiles();
		
		// (count file won't exist if blog has not been 'initialized')
		if ( file_exists( $count ) ) {
			$date = date("d.m.y.");
			$tstamp  = mktime(0, 0, 0, date("m"), date("d")-1, date("y"));
			$yesterday = date("d.m.y.", $tstamp);
			$time = date("H:i:s");
			$isempty = "readfile($count)";
			if ($isempty=="") {
				$emptyhandle = fopen($count,"w+");
				fputs($emptyhandle,"0|0|0|0|0");
				fclose($emptyhandle);
			}
			if ( file_exists( $count ) ) { 
				$counthandle = fopen($count,"r");
				$counter = fgets($counthandle, 1000);
				list($ctotalold,$dateold,$hits,$dateyesterday,$hitsyesterday ) = explode("|",$counter);
				fclose($counthandle);
			}
			
			$ctotalold++;
			$ctotal = $ctotalold;
			if ($dateold == $date) {
				$hits++;
			} elseif ($dateold == $yesterday) {
				$dateyesterday  = $dateold;
				$hitsyesterday  = $hits;
				$hits = 1;
			} else {
				$hits = 1;
				$dateyesterday = $yesterday;
				$hitsyesterday = 0;
			}
			$new_line = "$ctotal|$date|$hits|$dateyesterday|$hitsyesterday";
			
			$writecount = fopen($count,"w+");
			fputs($writecount,$new_line);
			fclose($writecount);
		}
	}
	
	// print functions
	function stat_total() {
		global $count, $aktip;
		
		checkip( $aktip );
		
		if ( file_exists( $count ) ) {    
			$handle = fopen($count,"r");
			while($counter = fgetcsv($handle, 1024, "|")) {
				$text = "$counter[0]";
			}
			fclose ($handle);
		} else {
			$text = "0";
		}
		
		return( $text );   
	}
	
	function stat_all() {
		global $count, $aktip;
		
		checkip( $aktip );
		
		if ( file_exists( $count ) ) {
			$allhandle = fopen($count,"r");
			while($counter = fgetcsv($allhandle, 1024, "|")) {
				// $text = $lang_string['counter_totalsidebar'] . ' <b>' . $counter[0] . '</b><br />' . $lang_string['counter_today'] . ' <b>' . $counter[2] . '</b><br />' . $lang_string['counter_yesterday'] . ' <b>' . $counter[4] . '</b><br />';
				$text = _sb('counter_totalsidebar') . ' <b>' . number_format( $counter[0], 0 ) . '</b><br />' . _sb('counter_today') . ' <b>' . number_format( $counter[2], 0 ) . '</b><br />' . _sb('counter_yesterday') . ' <b>' . number_format( $counter[4], 0 ) . '</b><br />';
			}
		
			fclose ($allhandle);
		} else {
			$text = _sb('counter_totalsidebar') . ' <b>0</b><br />' . _sb('counter_today') . ' <b>0</b><br />' . _sb('counter_yesterday') . ' <b>0</b><br />';
		}  
		return( $text );
	}
?>
