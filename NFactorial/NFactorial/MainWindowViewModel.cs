using GalaSoft.MvvmLight;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NFactorial
{
    public class MainWindowViewModel : ViewModelBase
    {
        public string NumberCountPerThread { get; set; }
        public string ThreadCount { get; set; }
        public string MaxElementQueueCount { get; set; }
        public string FactorialInput { get; set; }
        public string FactorialOutput { get; set; }
        public string FactorialOutputLength { get; set; }
        public string TimeTaken { get; set; }

        public string PartialProgressStatus { get; set; }
        public int PartialProgressValue { get; set; }
        public int PartialProgressMaximum { get; set; }

        public string TotalProgressStatus { get; set; }
        public int TotalProgressValue { get; set; }
        public int TotalProgressMaximum { get; set; }

        public MainWindowViewModel()
        {

        }
    }
}
