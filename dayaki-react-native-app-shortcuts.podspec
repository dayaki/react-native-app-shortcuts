require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = '@dayaki/react-native-app-shortcuts'
  s.version      = package['version']
  s.summary      = package['description']
  s.homepage     = package['homepage']
  s.license      = package['license']
  s.authors      = package['author']

  s.platforms    = { :ios => '11.0' }
  s.source       = { :git => 'https://github.com/your-npm-username/react-native-app-shortcuts.git', :tag => "v#{s.version}" }

  s.source_files = 'ios/RNAppShortcuts/**/*.{h,m}'

  s.dependency 'React-Core'
end
