#include "../SilkCasket.hpp"

using namespace std;
using namespace SilkCasket;

void printUsage()
{
    cout << "Usage: SilkCasket [option] [arguments...]\n";
    cout << "Options:\n";
    cout << "  -c, --compress <dir> <output> <blocksize> <key>\n";
    cout << "  -d, --decompress <archive> <output> <key>\n";
    cout << "  -h, --help\n";
}

int main(int argc, char *argv[])
{
    if (argc < 2)
    {
        printUsage();
        return 1;
    }

    string option = argv[1];
    if (option == "-h" || option == "--help")
    {
        printUsage();
        return 0;
    }

    if (option == "-c" || option == "--compress")
    {
        if (argc != 6)
        {
            printUsage();
            return 1;
        }

        filesystem::path targetPath = argv[2];
        filesystem::path outPath = argv[3];
        size_t blockSize = stoul(argv[4]);
        string key = argv[5];

        if (!filesystem::is_directory(targetPath))
        {
            cerr << "Error: The specified path is not a directory.\n";
            return 1;
        }

        bool entryEncryption = !key.empty();
        SilkCasket_compressDirectory(true, targetPath, outPath, {true, true, true, true, true}, blockSize, entryEncryption, key);
    }
    else if (option == "-d" || option == "--decompress")
    {
        if (argc != 5)
        {
            printUsage();
            return 1;
        }

        filesystem::path archivePath = argv[2];
        filesystem::path outPath = argv[3];
        string key = argv[4];

        releaseAllEntry(archivePath, outPath, key);
    }
    else
    {
        printUsage();
        return 1;
    }

    return 0;
}