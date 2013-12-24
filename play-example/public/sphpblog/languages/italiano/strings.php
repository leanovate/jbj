<?php
// Italian Language File
// (c) 2004 Valerio Guaglianone (alias valpenguin), valpenguin <at> gmail <dot> com, http://valpenguin.altervista.org/
// Simple PHP Version: 0.5.1
function sb_language( $page ="") {
global $lang_string;
// Language: Italiano
$lang_string['language'] = 'italiano';
$lang_string['locale'] = array('it_IT','ita','italian');
$lang_string['rss_locale'] = 'it-IT'; // New 0.4.8
// ISO Charset: ISO-8859-1
$lang_string['html_charset'] = 'ISO-8859-1';
$lang_string['php_charset'] = 'ISO-8859-1';
setlocale( LC_TIME, $lang_string['locale'] );
}
?>
