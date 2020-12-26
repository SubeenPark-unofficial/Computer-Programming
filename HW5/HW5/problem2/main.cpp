#include <iostream>
#include <string>
#include <utility>
#include <set>
#include <vector>
#include <tuple>
#include <unordered_map>
#include <cmath>
#include <algorithm>

/* =======START OF PRIME-RELATED HELPERS======= */
/*
 * The code snippet below AS A WHOLE does the primality
 * test and integer factorization. Feel free to move the
 * code to somewhere more appropriate to get your codes
 * more structured.
 *
 * You don't have to understand the implementation of it.
 * But if you're curious, refer to the sieve of Eratosthenes
 *
 * If you want to just use it, use the following 2 functions.
 *
 * 1) bool is_prime(int num):
 *     * `num` should satisfy 1 <= num <= 999999
 *     - returns true if `num` is a prime number
 *     - returns false otherwise (1 is not a prime number)
 *
 * 2) std::multiset<int> factorize(int num):
 *     * `num` should satisfy 1 <= num <= 999999
 *     - returns the result of factorization of `num`
 *         ex ) num = 24 --> result = { 2, 2, 2, 3 }
 *     - if `num` is 1, it returns { 1 }
 */

const int PRIME_TEST_LIMIT = 999999;
int sieve_of_eratosthenes[PRIME_TEST_LIMIT + 1];
bool sieve_calculated = false;

void make_sieve() {
    sieve_of_eratosthenes[0] = -1;
    sieve_of_eratosthenes[1] = -1;
    for(int i=2; i<=PRIME_TEST_LIMIT; i++) {
        sieve_of_eratosthenes[i] = i;
    }
    for(int i=2; i*i<=PRIME_TEST_LIMIT; i++) {
        if(sieve_of_eratosthenes[i] == i) {
            for(int j=i*i; j<=PRIME_TEST_LIMIT; j+=i) {
                sieve_of_eratosthenes[j] = i;
            }
        }
    }
    sieve_calculated = true;
}

bool is_prime(int num) {
    if (!sieve_calculated) {
        make_sieve();
    }
    return sieve_of_eratosthenes[num] == num;
}

std::multiset<int> factorize(int num) {
    if (!sieve_calculated) {
        make_sieve();
    }
    std::multiset<int> result;
    while(num > 1) {
        result.insert(sieve_of_eratosthenes[num]);
        num /= sieve_of_eratosthenes[num];
    }
    if(result.empty()) {
        result.insert(1);
    }
    return result;
}


/* =======END OF PRIME-RELATED HELPERS======= */

/* =======START OF std::string LITERALS======= */
/* Use this code snippet if you want */

const std::string MAXIMIZE_GAIN = "Maximize-Gain";
const std::string MINIMIZE_LOSS = "Minimize-Loss";
const std::string MINIMIZE_REGRET = "Minimize-Regret";

/* =======END OF std::string LITERALS======= */


/* =======START OF TODOs======= */

std::pair<int, int> number_fight(int a, int b) {
    // TODO 2-1
    std::multiset<int> fa = factorize(a);
    std::multiset<int> fb = factorize(b);
    std::multiset<int> fList;
    std::multiset<int>::iterator it;

    int tempPrime = 0, mulPrime = 1;

    for (it = fa.begin(); it != fa.end(); it++)
    {
        tempPrime = *it;

        if (!is_prime(tempPrime))
        {
            continue;
        }

        if (fb.count(tempPrime) > 0)
        {
            if (fList.count(tempPrime) == 0)
            {
                fList.insert(tempPrime);
            }
        }
    }

    for (it = fList.begin(); it != fList.end(); it++)
    {
        mulPrime *= *it;
    }

    return std::pair<int, int>(a / mulPrime, b / mulPrime);
}

std::pair<int, int> number_vs_number(int a, int b) {
    // TODO 2-2
    const int SELECT_FACTOR = 7;

    std::pair<int, int> calcResult[2][2];
    std::pair<int, int> fightDecision(0, 0); // 0 = not to fight, 1 = fight
    int damage = 0, halfDamage = 0;

    calcResult[0][0] = std::pair<int, int>(a, b);
    calcResult[1][1] = number_fight(a, b);

    // A attack B result
    std::multiset<int> bf = factorize(b);

    damage = b - calcResult[1][1].second;
    halfDamage = (int)floor(damage / 2);
    calcResult[1][0] = (bf.count(SELECT_FACTOR) > 0) ? std::pair<int, int>(a - halfDamage, b - halfDamage) :
                                                    std::pair<int, int>(a, b - damage);

    // B attack A result
    std::multiset<int> af = factorize(a);

    damage = a - calcResult[1][1].first;
    halfDamage = (int)floor(damage / 2);
    calcResult[0][1] = (af.count(SELECT_FACTOR) > 0) ? std::pair<int, int>(a - halfDamage, b - halfDamage) :
                                                    std::pair<int, int>(a - damage, b);    


    int conditionalDecision[2] = { 0, 0 };
    // decision A
    for (int i = 0; i < 2; ++i)
    {
        conditionalDecision[i] = (calcResult[1][i].first >= calcResult[0][i].first) ? 1 : 0;
    }

    // // if B will not to fight
    // conditionalDecision[0] = (calcResult[1][0].first >= calcResult[0][0].first) ? 1 : 0;

    // // if B will fight
    // conditionalDecision[1] = (calcResult[1][1].first >= calcResult[0][1].first) ? 1 : 0;

    fightDecision.first = (conditionalDecision[0] == conditionalDecision[1]) ? conditionalDecision[0] :
                            ((a > b) ? 0 : 1);


    // decision B
    for (int i = 0; i < 2; ++i)
    {
        conditionalDecision[i] = (calcResult[i][1].second >= calcResult[i][0].second) ? 1 : 0;
    }

    fightDecision.second = (conditionalDecision[0] == conditionalDecision[1]) ? conditionalDecision[0] :
                            ((b > a) ? 0 : 1);

    return calcResult[fightDecision.first][fightDecision.second];
}

int decide_card(std::unordered_map<int, std::pair<int,int>> &a_result, std::string type_a) {
    int a_value;
    if (MAXIMIZE_GAIN == type_a) {
        int maxi = INT32_MIN;
        a_value = INT32_MAX;
        for (auto &a_e: a_result) {
            if (maxi <= a_e.second.second) {
                if (maxi == a_e.second.second) {
                    if (a_value > a_e.first) {
                        a_value = a_e.first;
                    }
                } else {
                    a_value = a_e.first;
                }
                maxi = a_e.second.second;
            }
        }
    } else if (MINIMIZE_LOSS == type_a) {
        int maxi = INT32_MIN;
        a_value = INT32_MAX;
        for (auto &a_e: a_result) {
            if (maxi <= a_e.second.first) {
                if (maxi == a_e.second.first) {
                    if (a_value > a_e.first) {
                        a_value = a_e.first;
                    }
                } else {
                    a_value = a_e.first;
                }
                maxi = a_e.second.first;
            }
        }

    } else if (MINIMIZE_REGRET == type_a) {

//        if (a_result.size() == 1) return a_result.begin()->first;

        int mini = INT32_MAX;
        a_value = INT32_MAX;

        for (auto a_it = a_result.begin(); a_it != a_result.end(); a_it++) {
            int maxOther = INT32_MIN;
            for (auto o_it = a_result.begin(); o_it != a_result.end(); o_it++) {
                if (a_it != o_it) maxOther = std::max(maxOther, o_it->second.second);
            }
            int reg = a_it->second.first - maxOther;

            if (reg < mini) {
                a_value = a_it->first;
                mini = reg;
            } else if(reg == mini) {
                if(a_value > a_it->first) {
                    a_value = a_it->first;
                }
            }
        }
    }
    return a_value;
}

std::pair<std::multiset<int>, std::multiset<int>> player_battle(
    std::string type_a, std::multiset<int> a, std::string type_b, std::multiset<int> b
) {
    if (a.size() == 0 || b.size() == 0)
        std::pair<std::multiset<int>, std::multiset<int>>(a, b);
    // TODO 2-3
    std::unordered_map<int, std::unordered_map<int, std::pair<int, int>>> result;

    for (auto a_e : a) {
        for (auto b_e : b) {
            result[a_e][b_e] = number_vs_number(a_e, b_e);
            result[a_e][b_e].first -= a_e;
            result[a_e][b_e].second -= b_e;
        }
    }

    std::unordered_map<int, std::pair<int, int>> a_result;
    for (auto a_e : a) {
        int mini = INT32_MAX;
        int maxi = INT32_MIN;
        for (auto b_e : b) {
            mini = std::min(result[a_e][b_e].first, mini);
            maxi = std::max(result[a_e][b_e].first, maxi);
        }
        a_result[a_e].first = mini;
        a_result[a_e].second = maxi;
    }

    std::unordered_map<int, std::pair<int, int>> b_result;
    for (auto b_e : b) {
        int mini = INT32_MAX;
        int maxi = INT32_MIN;
        for (auto a_e : a) {
            mini = std::min(result[a_e][b_e].second, mini);
            maxi = std::max(result[a_e][b_e].second, maxi);
        }
        b_result[b_e].first = mini;
        b_result[b_e].second = maxi;
    }

    int a_card = decide_card(a_result, type_a);
    int b_card = decide_card(b_result, type_b);

    a.erase(a_card);
    b.erase(b_card);

    auto tmp = number_vs_number(a_card,b_card);
    a.insert(tmp.first);
    b.insert(tmp.second);

    return std::pair<std::multiset<int>, std::multiset<int>>(a,b);


}

std::pair<std::multiset<int>, std::multiset<int>> player_vs_player(
    std::string type_a, std::multiset<int> a, std::string type_b, std::multiset<int> b
) {
    // TODO 2-4
    std::pair<std::multiset<int>, std::multiset<int>> result;
    std::pair<std::multiset<int>, std::multiset<int>> previousResult;
    previousResult.first = a;
    previousResult.second = b;
    while (true)
    {
        result = player_battle(type_a, previousResult.first, type_b, previousResult.second);
        if (result == previousResult)
        {
            break;
        }
        previousResult = result;
    }
    return result;
}

int _tournament(std::vector<std::pair<std::string, std::multiset<int>>> &players, int s, int e) {
    if(s >= e)
        return e;

    int left_winner = _tournament(players, s, (s+e)/2);
    int right_winner = _tournament(players, (s+e)/2+1, e);

    auto &lw_p = players[left_winner];
    auto &rw_p = players[right_winner];
    auto ret = player_vs_player(lw_p.first, lw_p.second, rw_p.first, rw_p.second);
    int l_sum = 0;
    for(auto n : ret.first)
        l_sum += n;
    int r_sum = 0;
    for(auto n : ret.second)
        r_sum += n;

    return l_sum > r_sum ? left_winner : right_winner;
}

int tournament(std::vector<std::pair<std::string, std::multiset<int>>> players) {
    // TODO 2-5
    return _tournament(players, 0, players.size()-1);
}



int steady_winner(std::vector<std::pair<std::string, std::multiset<int>>> players) {
    // TODO 2-6
    int size = players.size();
    std::vector<int> winCount(size);
    std::vector<std::pair<std::string, std::multiset<int>>> tournamentList(size);

    for (int i = 0; i < size; ++i)
    {
        tournamentList.clear();

        for (int k = 0; k < size; ++k)
        {
            int index = k + i;

            tournamentList.push_back(players[(index < size) ? index : (index - size)]);
        }

        int winner = tournament(tournamentList) + i;

        winCount[winner] += 1;
    }

    int maxIndex = 0, maxValue = 0;

    for (int i = 0; i < winCount.size(); ++i)
    {
        if (maxValue < winCount[i])
        {
            maxIndex = i;
            maxValue = winCount[i];
        }
    }

    return maxIndex;
}

/* =======END OF TODOs======= */

/* =======START OF THE MAIN CODE======= */
/* Please do not modify the code below */

typedef std::pair<std::string, std::multiset<int>> player;

player scan_player() {
    std::multiset<int> numbers;
    std::string player_type; int size;
    std::cin >> player_type >> size;
    for(int i=0;i<size;i++) {
        int t; std::cin >> t; numbers.insert(t);
    }
    return make_pair(player_type, numbers);
}

void print_multiset(const std::multiset<int>& m) {
    for(int number : m) {
        std::cout << number << " ";
    }
    std::cout << std::endl;
}

int main() {
    int question_number; std::cin >> question_number;
    if (question_number == 1) {
        int a, b; std::cin >> a >> b;
        std::tie(a, b) = number_fight(a, b);
        std::cout << a << " " << b << std::endl;
    } else if (question_number == 2) {
        int a, b; std::cin >> a >> b;
        std::tie(a, b) = number_vs_number(a, b);
        std::cout << a << " " << b << std::endl;
    } else if (question_number == 3 || question_number == 4) {
        auto a = scan_player();
        auto b = scan_player();
        std::multiset<int> a_, b_;
        if (question_number == 3) {
            tie(a_, b_) = player_battle(
                a.first, a.second, b.first, b.second
            );
        } else {
            tie(a_, b_) = player_vs_player(
                a.first, a.second, b.first, b.second
            );
        }
        print_multiset(a_);
        print_multiset(b_);
    } else if (question_number == 5 || question_number == 6) {
        int num_players; std::cin >> num_players;
        std::vector<player> players;
        for(int i=0;i<num_players;i++) {
            players.push_back(scan_player());
        }
        int winner_id;
        if (question_number == 5) {
            winner_id = tournament(players);
        } else {
            winner_id = steady_winner(players);
        }
        std::cout << winner_id << std::endl;
    }
    return 0;
}



/* =======END OF MAIN CODE======= */