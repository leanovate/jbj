<?php

	// Danish Language File
	// (c) 2005-2006 Jan Normann Nielsen, spam <at> dubbekarl <dot> dk
	// (c) 2004 Thomas Petersen, thomasp <at> nsd <dot> dk
	//
	// Simple PHP Version: 0.4.8
	// Language Version:   0.4.8
		
	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: Danish
		$lang_string['language'] = 'dansk';
		
		$lang_string[ 'locale' ] = array('da_DK','danish');
		$lang_string['rss_locale'] = 'da-DK';
		
		// ISO Charset: ISO-8859-15 - Se http://us4.php.net/manual/en/function.htmlspecialchars.php for valg af ISO-8859-15-tegnsæt.
		$lang_string['html_charset'] = 'ISO-8859-15';
		$lang_string['php_charset'] = 'ISO-8859-15';
		
		setlocale( LC_TIME, $lang_string['locale'] );
		
	}
?>
