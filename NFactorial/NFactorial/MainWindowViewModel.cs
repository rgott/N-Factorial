using System;
using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using System.Collections.Generic;
using System.Linq;
using System.Numerics;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Input;
using System.Collections.Concurrent;
using System.ComponentModel;
using System.Diagnostics;
using System.Windows;

namespace NFactorial
{
    public class MainWindowViewModel : ViewModelBase
    {
        public int FactorialInput { get; set; } = 123456;

        public BigInteger _FactorialOutput;
        public BigInteger FactorialOutput
        {
            get
            {
                return _FactorialOutput;
            }
            set
            {
                _FactorialOutput = value;
                RaisePropertyChanged();
            }
        }

        public int _FactorialOutputLength;
        public int FactorialOutputLength
        {
            get
            {
                return _FactorialOutputLength;
            }
            set
            {
                _FactorialOutputLength = value;
                RaisePropertyChanged();
            }
        }

        private long _TimeTaken;
        public long TimeTaken
        {
            get
            {
                return _TimeTaken;
            }
            set
            {
                _TimeTaken = value;
                RaisePropertyChanged();
            }
        }

        private string _TotalProgressStatus;
        public string TotalProgressStatus
        {
            get
            {
                return _TotalProgressStatus;
            }
            set
            {
                _TotalProgressStatus = value;
                RaisePropertyChanged();
            }
        }

        private int _TotalProgressValue;
        public int TotalProgressValue
        {
            get
            {
                return _TotalProgressValue;
            }
            set
            {
                _TotalProgressValue = value;
                RaisePropertyChanged();
            }
        }
        private int _TotalProgressMaximum;
        public int TotalProgressMaximum
        {
            get
            {
                return _TotalProgressMaximum;
            }
            set
            {
                _TotalProgressMaximum = value;
                RaisePropertyChanged();
            }
        }

        public CancellationTokenSource CancellationToken { get; set; }

        public ICommand CopyCmd { get; set; }
        public ICommand CancelCmd { get; set; }
        public ICommand ComputeCmd { get; set; }
        

        public MainWindowViewModel()
        {
            CopyCmd = new RelayCommand(Copy);
            CancelCmd = new RelayCommand(Cancel);
            ComputeCmd = new RelayCommand(Compute);
        }

        private void Copy()
        {
            Clipboard.SetText(FactorialOutput.ToString());
        }

        private void Cancel()
        {
            CancellationToken.Cancel();
        }

        private void Compute()
        {
            TotalProgressMaximum = FactorialInput;
            TotalProgressStatus = "STARTING";
            TotalProgressValue = 0;
            FactorialOutput = BigInteger.One;

            CancellationToken = new CancellationTokenSource();
            
            BackgroundWorker bw = new BackgroundWorker();
            bw.DoWork += Bw_DoWork;
            bw.RunWorkerAsync();
        }

        private void Bw_DoWork(object sender, DoWorkEventArgs e)
        {
            Stopwatch timer = new Stopwatch();
            timer.Start();

            var next = NextNumber(FactorialInput);

            try
            {
                FactorialOutput = next
                    .AsParallel()
                    .WithCancellation(CancellationToken.Token)
                    .Aggregate(BigInteger.One, (num1, num2) =>
                {
                    TotalProgressValue++;
                    return num1 * num2;
                });
            } catch (OperationCanceledException)
            {
                TotalProgressStatus = "Cancelled";
                return;
            }

            FactorialOutputLength = FactorialOutput.ToString().Length;
            TotalProgressStatus = "Finished";

            timer.Stop();
            TimeTaken = timer.ElapsedMilliseconds;

            IEnumerable<BigInteger> NextNumber(BigInteger num)
            {
                while (!num.IsZero) yield return num--;
            }
        }
    }
}
