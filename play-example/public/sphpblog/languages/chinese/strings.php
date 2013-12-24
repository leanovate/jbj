<?php
	// Simplified Chinese Language File
	// (c) 2004 Jfly, jflycn <at> hotmail <dot> com
	//
	// Simple PHP Version: 0.4.6
	// Language Version:   0.4.6.2
	
	
	function sb_language( $page ="") {
		global $language, $html_charset, $php_charset, $lang_string;
			
		// Language: Simplified Chinese
		$lang_string[ 'language' ] = 'chinese';
		
		$lang_string[ 'locale' ] = array('zh_CN.UTF-8','zh_CN.GB2312','chs','chinese-simplified');
		$lang_string['rss_locale'] = 'zh-CN'; // New 0.4.8
		
		// ISO Charset: UTF-8
		$lang_string[ 'html_charset' ] = 'UTF-8';
		$lang_string[ 'php_charset' ] = 'UTF-8';
		
		setlocale(LC_TIME, $lang_string[ 'locale' ] );
		

	}
		
?>
