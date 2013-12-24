<?php 
	// Sitemap support
	// (c) 2005 Javier Gutiérrez Chamorro (Guti), guti <at> ya <dot> com
	//
	// Simple PHP Version: 0.4.4
	// RSS Version:   0.4.4
	
	// Include Required Functions
	
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );
	
	read_config();
	
	require_once('languages/' . $blog_config->getTag('BLOG_LANGUAGE') . '/strings.php');
	sb_language( 'index' );
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	
	// ----
	// HTML
	// ----
	
	// Read entries by month, year and/or day. Generate HTML output.
	//
	// Used for the main Index page.
	global $blog_config, $user_colors, $sb_info;
	
	$entry_file_array = blog_entry_listing();

	$base_url = dirname(sb_curPageURL()) . '/';

	header('Content-type: text/xml');
	echo "<?xml version='1.0' encoding='UTF-8'?>\n";
	echo "<?xml-stylesheet type='text/xsl' href='templates/gss.xsl'?>\n";
	echo "<urlset xmlns=\"http://www.google.com/schemas/sitemap/0.84\">\n";
	write_map( $base_url . 'atom.php', gmdate( 'Y-m-d', time() ), 'always', 0.9 );
	write_map( $base_url . 'contact.php', gmdate( 'Y-m-d', time() ), 'monthly', 0.1 );
	write_map( $base_url . 'index.php', gmdate( 'Y-m-d', time() ), 'always', 0.9 );
	write_map( $base_url . 'login.php', gmdate( 'Y-m-d', time() ), 'monthly', 0.1 );
	write_map( $base_url . 'rdf.php', gmdate( 'Y-m-d', time() ), 'always', 0.9 );
	write_map( $base_url . 'rss.php', gmdate( 'Y-m-d', time() ), 'always', 0.9 );
	write_map( $base_url . 'search.php', gmdate( 'Y-m-d', time() ), 'monthly', 0.1 );
	write_map( $base_url . 'stats.php', gmdate( 'Y-m-d', time() ), 'daily', 0.1 );
	//Add more static pages here
	
	// Read entry files
	$max_entries=min( 10000, count( $entry_file_array ) );
	$i=0;
	while ( $i<$max_entries ) {
		list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
		$contents=blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
		write_map( $base_url . 'index.php?entry=' . sb_strip_extension( $entry_filename ), gmdate( 'Y-m-d', $contents[ 'DATE' ] ), 'weekly', 0.5 );
		if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') ) {
			write_map( $base_url . 'comments.php?y=' . $year_dir . '&amp;m=' . $month_dir . '&amp;entry=' . sb_strip_extension( $entry_filename ), gmdate( 'Y-m-d', time() ), 'daily', 0.7 );
		}
		if ( $blog_config->getTag('BLOG_ENABLE_TRACKBACKS') ) {
			write_map( $base_url . 'trackback.php?y=' . $year_dir . '&amp;m=' . $month_dir . '&amp;entry=' . sb_strip_extension( $entry_filename ) . '&amp;__mode=html', gmdate( 'Y-m-d', time() ), 'daily', 0.1 );
		}
		$i++;
	}
	// TODO Need to dump static pages
	echo "</urlset>";
	
	function write_map( $loc, $lastmod, $changefreq, $priority )
	{
		echo "\t<url>\n";
		echo "\t\t<loc>" . $loc . "</loc>\n";
		echo "\t\t<lastmod>" . $lastmod . "</lastmod>\n";
		echo "\t\t<changefreq>" . $changefreq . "</changefreq>\n";
		echo "\t\t<priority>" . $priority . "</priority>\n";
		echo "\t</url>\n";
	}
?>
