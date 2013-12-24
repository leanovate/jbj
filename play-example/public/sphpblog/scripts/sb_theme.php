<?php

  // Simple PHP Blog
  // Version 0.3.5 - 06/04/2004
  // ------------------------------
  // Created by: Alexander Palmo, apalmo <at> bigevilbrain <dot> com
  //
  // The Simple PHP Blog is released under the GNU Public License.
  //
  // You are free to use and modify the Simple PHP Blog. All changes
  // must be uploaded to SourceForge.net under Simple PHP Blog or
  // emailed to apalmo <at> bigevilbrain <dot> com
  //
  // Credit should be give to the original author and the Simple PHP Blog
  // logo graphic must appear on the site and link to the project
  // on SourceForge.net

	// ------------------
	// Theme Menu Display
	// ------------------

	function page_generated_in () {
		// Returns "Page Generated x.xxxx in seconds"
		global $page_timestamp;

		$str = str_replace ( '%s', round( getmicrotime() - $page_timestamp, 4 ), _sb('page_generated_in') );
		
		if ( $GLOBALS['blog_config']->getTag('BLOG_FOOTER_COUNTER') ) {
			$str  .= '&nbsp;|&nbsp;' . _sb('counter_total') . stat_total();
		}

		return ( $str );
	}
	
	function get_user_color( $key, $default="") {
		$color = $GLOBALS['user_colors'][$key];
		if (isColor($color,0)) { return $color;	}
		return $default;
	}
		
	function isColor($value, $empty) {
		if (strlen($value) == 3) { return preg_match('/^[a-f0-9]{3}$/i', $value); }
		if (strlen($value) == 6) { return preg_match('/^[a-f0-9]{6}$/i', $value); }
		return $empty;
	}
?>
