<?php
	// Tamil Language Translation(s)
	// Unknown (0.3.7)
	
	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: Tamil
		$lang_string['language'] = 'tamil';
		
		$lang_string[ 'locale' ] = array('ta_IN', 'tamil');
		$lang_string['rss_locale'] = 'ta-IN'; // New 0.4.8
		
		// ISO Charset: UTF-8
		$lang_string['html_charset'] = 'UTF-8';
		$lang_string['php_charset'] = 'UTF-8';		

		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}
		
?>
