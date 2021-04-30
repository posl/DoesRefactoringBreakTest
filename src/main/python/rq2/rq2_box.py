import copy
import math
from math import comb

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from scipy.stats import mannwhitneyu, ranksums

labels = {
    'Change Return Type': "Change\nReturn Type",
    'Add Parameter': 'Add\nParameter',
    'Rename Method': "Rename\nMethod",
    # 'Extract And Move Method': 'Extract And\nMove Method',
    'Move Class': "Move\nClass",
    'Rename Class': "Rename\nClass",
}

# To make violinplot
def plot(lines_per_type):
    lines_dict = [lines_per_type[lab] for lab in
                  labels.keys()]  # We do not use the refactoring types that heppen a few times
    fig, ax = plt.subplots()
    bp = sns.violinplot(data=lines_dict, jitter=True, color='gray', ax=ax, cut=0)
    plt.subplots_adjust(left=0.075, bottom=0.15, right=0.95, top=0.96, wspace=0.15, hspace=0.15)
    ax.set_xticklabels(labels.values())
    plt.xlabel('Refactoring Type')
    plt.ylabel('#Changed Lines')
    plt.grid()

    fig.savefig("Ref-Changed_violinplot.pdf")
# Calculate statistical significant deference with mann-whitney u test
threshold = 0.01
def calc_ss(lines_per_type):
    ss = {}
    tmp_labs = list(labels.keys())  # to reduce redundant combinations
    combination_num = comb(len(labels.keys()), 2)
    for val1 in labels.keys():
        tmp_labs.remove(val1)
        for val2 in tmp_labs:
            values_1 = lines_per_type[val1]
            values_2 = lines_per_type[val2]
            u_stats, p = mannwhitneyu(values_1, values_2, use_continuity=True, alternative='two-sided')
            if p < threshold/combination_num:# Bonferroni correction
                print(val1, val2, p)
                z, _ = ranksums(values_1, values_2)
                # effect calc from z-stats
                effect_size = z / math.sqrt(len(values_1) + len(values_2))
                print(effect_size)
                #cliffs_delta (not used)
                cliffs_delta = ((2 * u_stats) / (len(values_1) * len(values_2))) - 1
                print(cliffs_delta)

    pass


def main():
    df = pd.read_csv("fixlines.csv")
    groups = df.groupby('RefactoringType')
    print(groups.describe())
    lines_per_type = {}
    for name, group in groups:
        lines_per_type[name] = group['Changed_Lines'].values
    plot(lines_per_type)
    calc_ss(lines_per_type)


if __name__ == '__main__':
    main()
