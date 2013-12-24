<?php
	// Japanese Language Translations
	// (c) 2004 Sanshiro Akiyama (0.3.7)
	// (c) 2007 Takeshi Hamasaki (0.5.1)
	
	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: Japanese
		$lang_string['language'] = 'japanese';
		
		$lang_string['locale'] = array('ja_JP', 'japanese', 'jpn');
		$lang_string['rss_locale'] = 'ja-JP'; // New 0.4.8
		
		// ISO Charset: EUC-JP
		$lang_string['html_charset'] = 'EUC-JP';
		$lang_string['php_charset'] = 'EUC-JP';
		
        setlocale(LC_CTYPE, 'Japanese_Japan.20932' );
		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		

	}
		
?>
