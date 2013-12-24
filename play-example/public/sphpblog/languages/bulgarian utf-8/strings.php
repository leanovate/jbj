<?php
	      // Bulgarian Language Translation(s)
	      // (c) 2005 Lucy Pearl, lusinda <at> gbg <dot> bg	
            //

	      // Simple PHP Version: 0.4.1
	      // Language Version:   0.4.1.0

	      function sb_language( $page ="") {
		global $lang_string;
			
		// Language: English
		$lang_string['language'] = 'bulgarian';
		
		$lang_string['locale'] = array('bg_BG', 'bulgaria', 'bul');
		$lang_string['rss_locale'] = 'bg-BG'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'utf-8';
		$lang_string['php_charset'] = 'utf-8';
		
		setlocale(LC_TIME, $lang_string['locale'] );
		
	}
		
?>
