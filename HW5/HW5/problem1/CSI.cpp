#include "CSI.h"
#include <fstream>

Complex::Complex(): real(0), imag(0) {}

CSI::CSI(): data(nullptr), num_packets(0), num_channel(0), num_subcarrier(0) {}

CSI::~CSI() {
    if(data) {
        for(int i = 0 ; i < num_packets; i++) {
            delete[] data[i];
        }
        delete[] data;
    }
}

int CSI::packet_length() const {
    return num_channel * num_subcarrier;
}

void CSI::print(std::ostream& os) const {
    for (int i = 0; i < num_packets; i++) {
        for (int j = 0; j < packet_length(); j++) {
            os << data[i][j] << ' ';
        }
        os << std::endl;
    }
}

std::ostream& operator<<(std::ostream &os, const Complex &c) {
    // TODO: problem 1.1
    os << c.real << "+" << c.imag << "i";

    return os;
}

void read_csi(const char* filename, CSI* csi) {
    // TODO: problem 1.2
    int lineIndex = 0;
    int packetIndex = 0;
    int channelIndex = 0;
    int subcarrierIndex = 0;
    int value = 0;
    int secondValue = 0;
    std::string text = "";
    std::ifstream rawFile(filename);

    if (rawFile.is_open())
    {
        while (!rawFile.eof())
        {
            std::getline(rawFile, text);

            value = std::stoi(text);

            if (lineIndex == 3)
            {
                csi->data = new Complex*[(csi->num_packets)];

                for (int i = 0; i < (csi->num_packets); ++i)
                {
                    csi->data[i] = new Complex[(csi->num_subcarrier) * (csi->num_channel)];
                }
            }

            if (lineIndex == 0)
            {
                csi->num_packets = value;
            }
            else if (lineIndex == 1)
            {
                csi->num_channel = value;
            }
            else if (lineIndex == 2)
            {
                csi->num_subcarrier = value;
            }
            else
            {
                std::getline(rawFile, text);

                secondValue = std::stoi(text);

                Complex c;
                c.real = value;
                c.imag = secondValue;

                csi->data[packetIndex][(csi->num_subcarrier) * channelIndex + subcarrierIndex] = c;

                channelIndex += 1;
                lineIndex += 1;

                if (channelIndex == (csi->num_channel))
                {
                    channelIndex = 0;
                    subcarrierIndex += 1;
                }
                if (subcarrierIndex == (csi->num_subcarrier))
                {
                    subcarrierIndex = 0;
                    packetIndex += 1;
                }

            }
            
            lineIndex += 1;
        }
    }
}

float** decode_csi(CSI* csi) {
    // TODO: problem 1.3
    int firstSize = csi->num_packets;
    int secondSize = csi->num_channel * csi->num_subcarrier;
    float **decodeList;

    decodeList = new float*[firstSize];

    for (int i = 0; i < firstSize; ++i)
    {
        decodeList[i] = new float[secondSize];

        for (int k = 0; k < secondSize; ++k)
        {
            Complex c = csi->data[i][k];

            decodeList[i][k] = sqrt((c.real * c.real) + (c.imag * c.imag));
        }
    }

    return decodeList;
}

float* get_std(float** decoded_csi, int num_packets, int packet_length) {
    // TODO: problem 1.4
    float *stdList = new float[num_packets];

    for (int i = 0; i < num_packets; ++i)
    {
        stdList[i] = standard_deviation(decoded_csi[i], packet_length);
    }

    return stdList;
}

void save_std(float* std_arr, int num_packets, const char* filename) {
    // TODO: problem 1.5
    std::ofstream outFile(filename);

    if (outFile.is_open())
    {
        for (int i = 0; i < num_packets; ++i)
        {
            outFile << std_arr[i] << ' ';
        }
    }
}

// convenience functions
float standard_deviation(float* data, int array_length) {
    float mean = 0, var = 0;
    for (int i = 0; i < array_length; i++) {
        mean += data[i];
    }
    mean /= array_length;
    for (int i = 0; i < array_length; i++) {
        var += pow(data[i]-mean,2);
    }
    var /= array_length;
    return sqrt(var);
}