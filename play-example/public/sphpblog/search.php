<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );

                if (empty($search_string)) {
                        // search start page
			$search = new Search();
                        $page_title = $search->getTitle();
                } else {
                        // SUBJECT
                        $page_title = _sb('search_result_title');
                }

	// ---------------
	// POST PROCESSING
	// ---------------
	
	global $search_string;
	$search_string = $_GET[ 'q' ];

	require_once('scripts/sb_header.php');
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $lang_string, $blog_config, $search_string;
		$entry_array = array();
		$search = new Search();

		if (empty($search_string)) {
			// search start page
			$entry_array[ 'subject' ] = $search->getTitle();
		} else {	
			// SUBJECT
			$entry_array[ 'subject' ] = _sb('search_result_title');
		}
		
		// PAGE CONTENT BEGIN
		ob_start();

?>
		<input style="float: right" type="button" value="Add to Search Bar" onclick='window.external.AddSearchProvider("<?php echo dirname(sb_curPageURL()) ?>/plugins/search.php");' />
<?php
		print $search->getContent();

		if (!empty($search_string)) {			
		echo ( str_replace( '%string', @htmlspecialchars( $search_string, ENT_QUOTES, $GLOBALS['lang_string']['php_charset'] ), _sb('search_result_instructions') ) . '<br />' );
		
		echo( '<hr />' );

		$output = search( $search_string, @$_GET[ 'n' ] );
		
		if ( $output ) {
			echo ( $output );
		} else {
			echo( _sb('not_found') );
		}
		}
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );	
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
