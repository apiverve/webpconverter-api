from setuptools import setup, find_packages

setup(
    name='apiverve_webpconverter',
    version='1.1.13',
    packages=find_packages(),
    include_package_data=True,
    install_requires=[
        'requests',
        'setuptools'
    ],
    description='WebP Converter transforms WebP images to classic formats like PNG and JPG, or converts other formats to WebP for better compression. Essential for handling modern web images.',
    author='APIVerve',
    author_email='hello@apiverve.com',
    url='https://apiverve.com/marketplace/webpconverter?utm_source=pypi&utm_medium=homepage',
    classifiers=[
        'Programming Language :: Python :: 3',
        'Operating System :: OS Independent',
    ],
    python_requires='>=3.6',
)
