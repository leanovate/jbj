#!/usr/bin/env ruby

require 'optparse'

optparse = OptionParser.new do |opts|
  opts.banner = "Usage: phpt_convert.rb file"

  opts.on('-h', '--help', 'Display this screen') do
    puts opts
    exit
  end
end

optparse.parse!

ARGV.each do |f|
  File.open(f, "r") do |file|
    mode = :initial

    file.each do |line|
      case mode
        when :initial
          case line
            when /--TEST--.*/
              mode = :testname
            when /--FILE--.*/
              puts "      script("
              print "         \"\"\""
              mode = :testscript
          end
        when :testname
          puts "    \"#{line.strip}\" in {"
          puts "      // #{f.sub(/.*php-src\/tests\//, '')}"
          mode = :initial
        when :testscript
          if ( line =~ /--EXPECT(F)?--.*/ )
            puts "\"\"\".stripMargin"
            puts "      ).result must haveOutput("
            print "         \"\"\""
            mode = :expect
          else
            print "#{line}"
            print "           |"
          end
        when :expect
          print "#{line}"
          print "           |"
      end
    end
    puts "\"\"\".stripMargin"
    puts "      )"
    puts "    }"
  end
end