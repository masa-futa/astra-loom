#!/usr/bin/env ruby

require 'xcodeproj'

# Open the Xcode project
project_path = 'astra-loom/astra-loom.xcodeproj'
project = Xcodeproj::Project.open(project_path)

# Get the main target
target = project.targets.first

# Get the main group (astra-loom folder in Xcode)
main_group = project.main_group.find_subpath('astra-loom', true)

# Create ViewModels group if it doesn't exist
viewmodels_group = main_group.find_subpath('ViewModels', true) || main_group.new_group('ViewModels')
viewmodels_group.set_source_tree('<group>')
viewmodels_group.set_path('ViewModels')

# Create Views group if it doesn't exist
views_group = main_group.find_subpath('Views', true) || main_group.new_group('Views')
views_group.set_source_tree('<group>')
views_group.set_path('Views')

# Files to add
files_to_add = [
  { group: viewmodels_group, path: 'ViewModels/StarViewModel.swift' },
  { group: viewmodels_group, path: 'ViewModels/SkyViewModel.swift' },
  { group: views_group, path: 'Views/SkyCanvasView.swift' }
]

# Add files to project
files_to_add.each do |file_info|
  file_path = "astra-loom/#{file_info[:path]}"

  # Check if file already exists in project
  existing_file = file_info[:group].files.find { |f| f.path == File.basename(file_info[:path]) }

  unless existing_file
    puts "Adding #{file_info[:path]} to project..."

    # Add file reference to group
    file_ref = file_info[:group].new_reference(File.basename(file_info[:path]))
    file_ref.set_source_tree('<group>')

    # Add to target's source build phase
    target.source_build_phase.add_file_reference(file_ref)
  else
    puts "#{file_info[:path]} already exists in project, skipping..."
  end
end

# Save the project
project.save

puts "\n✅ Files successfully added to Xcode project!"
puts "Please open the project in Xcode to verify."
