<?php
	// Portuguese Language Translation(s)
	// (c) 2004 Nuno Cardoso, nuno.cardoso@tugamail.com (0.3.7)

	function sb_language( $page"") {
		global $lang_string;
			
		// Language: English
		$lang_string['language'] = 'Portuguese';
		
		$lang_string[ 'locale' ] = array('pt_PT');
		$lang_string['rss_locale'] = 'pt-PT'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO8859-1';
		$lang_string['php_charset'] = 'ISO8859-1';
		
		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}
		
?>
