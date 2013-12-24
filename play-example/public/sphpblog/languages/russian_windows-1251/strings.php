<?php
	// Russian Language Translation(s)
	// (c) 2004 Artyom Pervukhin, trem <at> pm <dot> convex <dot> ru (0.3.7)

	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: English
		$lang_string['language'] = 'russian';
		
		$lang_string['locale'] = array('ru_RU', 'russia', 'rus');
		$lang_string['rss_locale'] = 'ru-RU'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'windows-1251';
		$lang_string['php_charset'] = 'windows-1251';
		
		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}
		
?>
