<?php
	// Standard SPHPBlog Setup
	// -------------------
	
	// Load/Include Functions
	/*
		Read Configuration (See scripts/sb_config.php for details.)
		--------------
		Loads values into $blog_config global associative array.
	*/
	require_once('scripts/sb_functions.php');
	
	/*
		Check Login (See scripts/sb_login.php for details)
		---------
		logged_in( false, true );		<-- Anyone can view this page.
		logged_in( true, true );	<-- Redirects to index.php if you are NOT logged in.
	*/
	global $logged_in;
	$logged_in = logged_in( true, true );


	// include standard HTML header file here
	require_once('scripts/sb_functions.php');	
	
	// -------------
	// POST PROCESSING
	// -------------
	
	/*
		Validate your GET and POST data here...
	*/
	
	// -----------
	// PAGE CONTENT
	// -----------
	
	/*
		Page Content
		----------
		Your actual page content will go INSIDE the
		page_content() function below. This function gets
		called from inside the theme_pagelayout() function
		at the bottom of this file...
	*/
	
	function page_content() {
		/*
			Page Content
			----------
			If you want your content to be wrapped inside the normal
			"Entry" box, then use the $entry_array associative array
			to pass your content to the theme_staticentry() function
			located in themes/{theme_name}/themes.php
		*/
		$entry_array = array();
		$entry_array[ 'subject' ] = 'Subject Line';
		$entry_array[ 'entry' ] = 'Body Content<br /><a href="http://www.google.com/">Google</a>';
		
		echo( theme_staticentry( $entry_array ) );
		
		// HTML Content
		// ----------
		// You can also break out of PHP here and use HTML:
		?>
			Here is some raw HTML content...<br /><br />-- Alex.
		<?php 
		// ...now we're back in PHP and we're still inside
		// the page_content function...
	}
	// include standard HTML footer here
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
