<?php

	// Magyar nyelvi file
	// (c) 2006 BuBukák Team
	//

	// Simple PHP Version: 0.4.1
	// Language Version:   0.4.1.0	

	function sb_language( $page ="") {
		global $lang_string;
		

		// Language: Magyar
		$lang_string['language'] = 'magyar';	
		
		$lang_string['locale'] = array('hu_HU', 'hu');
		$lang_string['rss_locale'] = 'hu-HU'; // New 0.4.8

		// ISO Charset: ISO-8859-2
		$lang_string['html_charset'] = 'ISO-8859-2';
		$lang_string['php_charset'] = 'ISO-8859-1';	

		setlocale( LC_TIME, $lang_string['locale'] );		

	}

?>
